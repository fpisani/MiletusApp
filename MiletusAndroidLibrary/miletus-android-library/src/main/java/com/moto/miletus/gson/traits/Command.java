/*************************************************************************
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
 *************************************************************************/

package com.moto.miletus.gson.traits;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Command
 */
public final class Command {

    @SerializedName("minimalRole")
    private String minimalRole;

    @SerializedName("parameters")
    private Map<String, Parameter> parameters;

    /**
     * getMinimalRole
     *
     * @return String
     */
    public String getMinimalRole() {
        return minimalRole;
    }

    /**
     * setMinimalRole
     *
     * @param minimalRole String
     */
    public void setMinimalRole(String minimalRole) {
        this.minimalRole = minimalRole;
    }

    /**
     * getParameters
     *
     * @return Map
     */
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    /**
     * setParameters
     *
     * @param parameters Map
     */
    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }
}
