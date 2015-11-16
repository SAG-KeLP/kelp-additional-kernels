/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.data.representation.structure;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * This class implement a Factory Design pattern to instantiate
 * <code>StructureElement</code> given a string representing it. </br></br>
 * <b>Note</b>:When a new class implementing a <code>StructureElement</code> is
 * added, it should be annotated with <code>@JsonTypeName("NEW_TYPE")</code>
 * that is used to determine the new element. The input String will be in the
 * form <code>NEW_TYPE##struct_content</code>
 * 
 * @author Simone Filice, Danilo Croce
 */
public class StructureElementFactory {

	public static final String TYPE_CONTENT_SEPARATOR = "##";

	private static Logger logger = LoggerFactory
			.getLogger(StructureElementFactory.class);
	private static StructureElementFactory instance = null;

	private final Map<String, Class<? extends StructureElement>> structureElementImplementations;
	static {
		try {
			instance = new StructureElementFactory();
		} catch (Exception e) {
			logger.error("StructureElementFactory cannot be initialized: "
					+ e.getMessage());
			System.exit(0);
		}
		;
	}

	private StructureElementFactory() throws InstantiationException,
			IllegalAccessException {
		structureElementImplementations = discoverAllStructureElementImplementations();

		logger.debug("StructureElement Implementations: {}",
				this.structureElementImplementations);
	}

	/**
	 * Retrieves all the implementations of the class
	 * <code>StructureElement</code> included in the current project
	 * 
	 * @return a Map of pairs StructureElement type identifier -
	 *         StructureElement class
	 */
	private Map<String, Class<? extends StructureElement>> discoverAllStructureElementImplementations()
			throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections("it");

		Set<Class<? extends StructureElement>> classes = reflections
				.getSubTypesOf(StructureElement.class);
		HashMap<String, Class<? extends StructureElement>> implementatios = new HashMap<String, Class<? extends StructureElement>>();

		for (Class<? extends StructureElement> implementation : classes) {
			if (Modifier.isAbstract(implementation.getModifiers())) {
				continue;
			}
			String structureElementAbbreviation = getStructureElementIdentifier(implementation);
			implementatios.put(structureElementAbbreviation, implementation);
		}
		return implementatios;
	}

	/**
	 * Returns an instance of the class <code>StructureElementFactory</code>
	 * 
	 * @return an instance of the class <code>StructureElementFactory</code>
	 */
	public static StructureElementFactory getInstance() {
		return instance;
	}

	/**
	 * Initializes and returns the structureElement described in
	 * <code>structureElementBody</code>
	 * 
	 * @param structureElementType
	 *            the identifier of the structureElement class to be
	 *            instantiated
	 * @param structureElementBody
	 *            the the textual description of the structureElement to be
	 *            instantiated
	 * @return the structureElement described in
	 *         <code>structureElementBody</code>
	 */
	protected StructureElement parseStructureElement(
			String structureElementType, String structureElementBody)
			throws InstantiationException {

		Class<? extends StructureElement> structureElementClass = this.structureElementImplementations
				.get(structureElementType);
		if (structureElementClass == null) {
			throw new IllegalArgumentException("unrecognized structureElement "
					+ structureElementType);
		}

		try {
			StructureElement structureElement = structureElementClass
					.newInstance();
			structureElement.setDataFromText(structureElementBody);
			return structureElement;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstantiationException("Cannot initialize "
					+ structureElementType + " structureElements: "
					+ " missing empty constructor of the class "
					+ structureElementClass.getSimpleName());
		}

	}

	public StructureElement parseStructureElement(
			String structureElementDescription) throws InstantiationException {
		int separator = structureElementDescription
				.indexOf(TYPE_CONTENT_SEPARATOR);
		String type;
		String content;
		if (separator == -1) {
			content = structureElementDescription;
			type = getDefaultType(structureElementDescription);
		} else {
			type = structureElementDescription.substring(0, separator);
			content = structureElementDescription.substring(separator
					+ TYPE_CONTENT_SEPARATOR.length());
		}
		return parseStructureElement(type, content);
	}

	private String getDefaultType(String structureElementDescription) {
		if (structureElementDescription
				.contains(LexicalStructureElement.POS_LEMMA_SEPARATOR)) {
			return getStructureElementIdentifier(LexicalStructureElement.class);
		}

		return getStructureElementIdentifier(UntypedStructureElement.class);
	}

	/**
	 * Returns the identifier of a given class
	 * 
	 * @param c
	 *            the class whose identifier is requested
	 * @return the class identifier
	 */
	public static String getStructureElementIdentifier(
			Class<? extends StructureElement> c) {
		String structureElementAbbreviation;
		if (c.isAnnotationPresent(JsonTypeName.class)) {
			JsonTypeName info = c.getAnnotation(JsonTypeName.class);
			structureElementAbbreviation = info.value();

		} else {
			structureElementAbbreviation = c.getSimpleName().toUpperCase();
		}
		return structureElementAbbreviation;
	}

	public static String getTextualRepresentation(StructureElement element) {
		String type = getStructureElementIdentifier(element.getClass());
		String content = element.getTextFromData();
		return type + TYPE_CONTENT_SEPARATOR + content;
	}

}
