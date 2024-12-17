/*
 * Copyright (C) 2024 Slobodan Zivanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slobodanzivanovic.jewel.util.prefs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * A preferences implementation that stores application preferences in XML format.
 * This class extends AbstractPreferences to provide persistent storage of
 * preferences data in XML files, with each preferences node stored in a separate
 * XML file within a designated preferences directory.
 *
 * @author Slobodan Zivanovic
 */
public class XMLPreferences extends AbstractPreferences {

	private Map<String, String> root;
	private Map<String, XMLPreferences> children;
	private boolean isRemoved = false;
	private File preferencesFile;

	/**
	 * Constructs a new XMLPreferences node.
	 *
	 * @param parent The parent preferences node, or null for the root
	 * @param name   The name of this preferences node
	 */
	public XMLPreferences(XMLPreferences parent, String name) {
		super(parent, name);

		root = new HashMap<>();
		children = new HashMap<>();

		try {
			File dir = new File(XMLPreferencesFactory.PREFERENCES_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			preferencesFile = new File(dir, name + ".xml");
			if (preferencesFile.exists()) {
				loadPreferences();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads preferences from the XML file associated with this node.
	 * The file is parsed and its contents are stored in the root map.
	 *
	 * @throws IOException If there an error reading or parsing the XML file
	 */
	private void loadPreferences() throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(preferencesFile);
			doc.getDocumentElement().normalize();

			NodeList entries = doc.getElementsByTagName("entry");
			for (int i = 0; i < entries.getLength(); i++) {
				Node node = entries.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String key = element.getAttribute("key");
					String value = element.getTextContent();
					root.put(key, value);
				}
			}
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Error loading preferences", e);
		}
	}

	/**
	 * Saves the current preferences to the XML file.
	 * Creates a new XML document containing all preferences in the root map
	 * and writes it to the file with proper formatting.
	 *
	 * @throws IOException If there an error creating or writing the XML file
	 */
	private void savePreferences() throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element rootElement = doc.createElement("preferences");
			doc.appendChild(rootElement);

			for (Map.Entry<String, String> entry : root.entrySet()) {
				Element entryElement = doc.createElement("entry");
				entryElement.setAttribute("key", entry.getKey());
				entryElement.setTextContent(entry.getValue());
				rootElement.appendChild(entryElement);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(preferencesFile);
			transformer.transform(source, result);

		} catch (ParserConfigurationException | TransformerException e) {
			throw new IOException("Error saving preferences", e);
		}
	}

	@Override
	protected void putSpi(String key, String value) {
		root.put(key, value);

		try {
			savePreferences();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getSpi(String key) {
		return root.get(key);
	}

	@Override
	protected void removeSpi(String key) {
		root.remove(key);

		try {
			savePreferences();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		isRemoved = true;
		preferencesFile.delete();
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return root.keySet().toArray(new String[0]);
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return children.keySet().toArray(new String[0]);
	}

	@Override
	protected AbstractPreferences childSpi(String name) {
		XMLPreferences child = children.get(name);
		if (child == null || child.isRemoved()) {
			child = new XMLPreferences(this, name);
			children.put(name, child);
		}
		return child;
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		if (isRemoved) {
			return;
		}
		try {
			savePreferences();
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		syncSpi();
	}
}
