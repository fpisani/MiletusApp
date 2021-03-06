/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Gustavo Frederico Temple Pedrosa -- gustavof@motorola.com
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.moto.miletus.application.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moto.miletus.application.OnRunCommandListener;
import com.moto.miletus.application.R;
import com.moto.miletus.application.utils.Strings;
import com.moto.miletus.wrappers.DeviceProvider;
import com.moto.miletus.ble.commands.SendExecuteGattCommand;
import com.moto.miletus.mdns.SendExecuteCommand;
import com.moto.miletus.application.utils.CustomExceptionHandler;
import com.moto.miletus.wrappers.CommandWrapper;
import com.moto.miletus.wrappers.ComponentWrapper;
import com.moto.miletus.wrappers.DeviceWrapper;
import com.moto.miletus.wrappers.ComponentProvider;

/**
 * Handles the RecyclerView
 */
public class CommandsFragment extends Fragment implements OnRunCommandListener {

    private static final String TAG = CommandsFragment.class.getSimpleName();

    private CommandsAdapter commandsAdapter;
    private DeviceWrapper mDevice;
    private ComponentWrapper mComponent;

    private final SendExecuteCommand.OnExecuteCommandResponse executeCommandResponse =
            new SendExecuteCommand.OnExecuteCommandResponse() {
                @Override
                public void onExecuteCommandResponse(boolean isSuccess) {
                    if (!isSuccess) {
                        Log.e(TAG, "Failure setting state.");
                        if (CommandsFragment.this.getView() != null) {
                            Snackbar.make(CommandsFragment.this.getView(),
                                    R.string.error_setting_state,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Log.i(TAG, "Success setting state!");
                    }
                }
            };

    private final SendExecuteGattCommand.OnBleExecuteResponse bleExecuteResponse =
            new SendExecuteGattCommand.OnBleExecuteResponse() {
                @Override
                public void onBleExecuteResponse(boolean isSuccess) {
                    if (!isSuccess) {
                        Log.e(TAG, "Failure setting state.");
                        if (CommandsFragment.this.getView() != null) {
                            Snackbar.make(CommandsFragment.this.getView(),
                                    R.string.error_setting_state,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Log.i(TAG, "Success setting state!");
                    }
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View commandsLayout = inflater.inflate(R.layout.fragment_commands, container, false);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this.getContext()));
        }

        RecyclerView recyclerViewCommands = (RecyclerView) commandsLayout.findViewById(R.id.commandsList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewCommands.setHasFixedSize(true);

        // use a linear layout manager
        recyclerViewCommands.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        commandsAdapter = new CommandsAdapter(this);
        recyclerViewCommands.setAdapter(commandsAdapter);
        recyclerViewCommands.setHasFixedSize(true);

        return commandsLayout;
    }

    /**
     * retrieveState
     *
     * @param state Bundle
     */
    private void retrieveState(@NonNull final Bundle state) {
        mComponent = state.getParcelable(Strings.EXTRA_KEY_DEVICE_COMPONENT);
        mDevice = state.getParcelable(Strings.EXTRA_KEY_DEVICE);

        if (mDevice == null
                || mComponent == null) {
            mDevice = ((DeviceProvider) getActivity()).getDevice();
            mComponent = ((ComponentProvider) getActivity()).getComponent();

            if (mDevice == null
                    || mComponent == null) {
                throw new IllegalArgumentException("Required arguments are null.");
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        retrieveState(savedInstanceState != null ? savedInstanceState : getActivity().getIntent().getExtras());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Strings.EXTRA_KEY_DEVICE, mDevice);
        outState.putParcelable(Strings.EXTRA_KEY_DEVICE_COMPONENT, mComponent);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        commandsAdapter.clear();

        for (CommandWrapper componentCommand : mComponent.getCommands()) {
            commandsAdapter.addComponent(componentCommand);
        }
    }

    @Override
    public void onRunCommand(final String command) {
        if (mDevice.getBleDevice() == null) {
            if (CommandsFragment.this.getView() != null) {
                Snackbar.make(CommandsFragment.this.getView(),
                        R.string.send_by_wifi,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
            final SendExecuteCommand sendExecuteCommand = new SendExecuteCommand(mDevice.getDevice(),
                    executeCommandResponse,
                    command);
            try {
                sendExecuteCommand.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (OutOfMemoryError e) {
                Log.e(TAG, e.toString());
                sendExecuteCommand.execute();
            }
        } else {
            if (CommandsFragment.this.getView() != null) {
                Snackbar.make(CommandsFragment.this.getView(),
                        R.string.send_by_ble,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
            final SendExecuteGattCommand sendExecuteCommand = new SendExecuteGattCommand(this.getContext(),
                    bleExecuteResponse,
                    mDevice.getBleDevice(),
                    command);
            try {
                sendExecuteCommand.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (OutOfMemoryError e) {
                Log.e(TAG, e.toString());
                sendExecuteCommand.execute();
            }
        }
    }
}
