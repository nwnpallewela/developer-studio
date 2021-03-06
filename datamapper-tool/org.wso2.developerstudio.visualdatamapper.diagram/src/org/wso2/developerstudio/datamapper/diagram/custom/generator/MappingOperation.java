/*
 * Copyright 2016 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.developerstudio.datamapper.diagram.custom.generator;

import java.util.List;

import org.wso2.developerstudio.datamapper.diagram.custom.model.DMOperation;
import org.wso2.developerstudio.datamapper.diagram.custom.model.DMVariable;

/**
 * This class represent a operation object with input and output variables
 *
 */
public class MappingOperation {

    private List<DMVariable> inputVariables;
    private List<DMVariable> outputVariables;
    private DMOperation operation;

    public MappingOperation(List<DMVariable> inputVariables, List<DMVariable> outputVariables, DMOperation operation) {
        this.inputVariables = inputVariables;
        this.outputVariables = outputVariables;
        this.operation = operation;
    }

    public List<DMVariable> getInputVariables() {
        return inputVariables;
    }

    public void setInputVariables(List<DMVariable> inputVariables) {
        this.inputVariables = inputVariables;
    }

    public List<DMVariable> getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(List<DMVariable> outputVariables) {
        this.outputVariables = outputVariables;
    }

    public DMOperation getOperation() {
        return operation;
    }

    public void setOperation(DMOperation operation) {
        this.operation = operation;
    }
}
