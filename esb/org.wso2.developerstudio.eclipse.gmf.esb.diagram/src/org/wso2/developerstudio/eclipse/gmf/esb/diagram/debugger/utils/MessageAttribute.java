/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.debugger.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageAttribute {

	private Map<String,MessageAttribute> childAttributes;

	public MessageAttribute(Map<String, MessageAttribute> childAttributes) {
		super();
		this.childAttributes = childAttributes;
	}
	
	public void put(String key,MessageAttribute attribute){
		childAttributes.put(key, attribute);
	}
	
	public MessageAttribute getAttribute(String key){
		return childAttributes.get(key);
	}
	
	public Set<String> getAttributes(){
		return childAttributes.keySet();
	}
	
	public ArrayList<String> getAttributeKeys(){
		Set<String> firstkeys = childAttributes.keySet();
		List<String> allKeys = new ArrayList<>();
		for (String key : firstkeys) {
			allKeys.add(key);
			if(childAttributes.get(key)!=null){
				ArrayList<String> temp = childAttributes.get(key).getAttributeKeys();
				allKeys.addAll(temp);
			}
		}
		
		return (ArrayList<String>) allKeys;
	}
	
	public void print(){
		Set<String> keySet = childAttributes.keySet();
		for (String key : keySet) {
			System.out.println(" ---> "+key);
			if(childAttributes.get(key) != null){
				System.out.println("{");
				childAttributes.get(key).print();
				System.out.println("}");
			}
		}
	}
	
	
	
}
