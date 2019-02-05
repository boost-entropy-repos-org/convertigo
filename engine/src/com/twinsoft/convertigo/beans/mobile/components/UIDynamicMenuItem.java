/*
 * Copyright (c) 2001-2019 Convertigo SA.
 * 
 * This program  is free software; you  can redistribute it and/or
 * Modify  it  under the  terms of the  GNU  Affero General Public
 * License  as published by  the Free Software Foundation;  either
 * version  3  of  the  License,  or  (at your option)  any  later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;  without even the implied warranty of
 * MERCHANTABILITY  or  FITNESS  FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */

package com.twinsoft.convertigo.beans.mobile.components;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.twinsoft.convertigo.beans.core.ITagsProperty;
import com.twinsoft.convertigo.beans.mobile.components.MobileSmartSourceType.Mode;
import com.twinsoft.convertigo.engine.Engine;
import com.twinsoft.convertigo.engine.EngineException;
import com.twinsoft.convertigo.engine.util.EnumUtils;
import com.twinsoft.convertigo.engine.util.VersionUtils;
import com.twinsoft.convertigo.engine.util.XMLUtils;

public class UIDynamicMenuItem extends UIDynamicElement implements ITagsProperty {

	private static final long serialVersionUID = 3562736859348770057L;

	public UIDynamicMenuItem() {
		super();
	}

	public UIDynamicMenuItem(String tagName) {
		super(tagName);
	}

	@Override
	public UIDynamicMenuItem clone() throws CloneNotSupportedException {
		UIDynamicMenuItem cloned = (UIDynamicMenuItem) super.clone();
		return cloned;
	}
	
	
	@Override
	public void preconfigure(Element element) throws Exception {
		super.preconfigure(element);
		
		String version = element.getAttribute("version");

		if (VersionUtils.compare(version, "7.5.1") < 0) {
			try {
				NodeList properties = element.getElementsByTagName("property");
				
				// If needed: migration of itemtitle from MobileSmartSourceType to String (scriptable)
				Element propElement = (Element) XMLUtils.findNodeByAttributeValue(properties, "name", "itemtitle");
				if (propElement != null) {
					Element valueElement = (Element) XMLUtils.findChildNode(propElement, Node.ELEMENT_NODE);
					if (valueElement != null) {
						Document document = valueElement.getOwnerDocument();
						Object content = XMLUtils.readObjectFromXml(valueElement);
						if (content instanceof MobileSmartSourceType) {
							MobileSmartSourceType itemTitle = (MobileSmartSourceType) content;
							String itemText = Mode.PLAIN.equals(itemTitle.getMode()) ? "'"+itemTitle.getSmartValue()+"'" : itemTitle.getSmartValue();
							Element newValueElement = (Element)XMLUtils.writeObjectToXml(document, itemText);
							propElement.replaceChild(newValueElement, valueElement);
							hasChanged = true;
							Engine.logBeans.warn("(UIDynamicMenuItem) 'itemtitle' has been updated for the object \"" + getName() + "\"");
						} else if (content instanceof String) {
							String itemTitle = (String) content;
							String itemText = "'"+itemTitle+"'";
							Element newValueElement = (Element)XMLUtils.writeObjectToXml(document, itemText);
							propElement.replaceChild(newValueElement, valueElement);
							hasChanged = true;
							Engine.logBeans.warn("(UIDynamicMenuItem) 'itemtitle' has been updated for the object \"" + getName() + "\"");
						}
					}
				}
			}
	        catch(Exception e) {
	            throw new EngineException("Unable to preconfigure the menuitem component \"" + getName() + "\".", e);
	        }
		}
	}

	@Override
	protected void addUIComponent(UIComponent uiComponent, Long after) throws EngineException {
        if (!(uiComponent instanceof UIAttribute)) {
            throw new EngineException("You cannot add component to this menu item");
        }
        super.addUIComponent(uiComponent, after);
	}
	
	/*
	 * The item's title
	 */
	private String itemtitle = "'item\\\\'s title'";
	
	public String getItemTitle() {
		return itemtitle;
	}

	public void setItemTitle(String itemtitle) {
		this.itemtitle = itemtitle;
	}

	/*
	 * The item's icon
	 */
	private String itemicon = "";
	
	public String getItemIcon() {
		return itemicon;
	}

	public void setItemIcon(String itemicon) {
		this.itemicon = itemicon;
	}
	
	/*
	 * The item's icon position
	 */
	private String itemiconPos = "";
	
	public String getItemIconPosition() {
		return itemiconPos;
	}

	public void setItemIconPosition(String itemiconPos) {
		this.itemiconPos = itemiconPos;
	}
	
	/*
	 * The page associated with item
	 */
	private String itempage = "";
	
	public String getItemPage() {
		return itempage;
	}

	public void setItemPage(String itempage) {
		this.itempage = itempage;
	}
	
	private String getPageName() {
		if (!itempage.isEmpty()) {
			try {
				return itempage.substring(itempage.lastIndexOf('.')+1);
			} catch (Exception e) {}
		}
		return "";
	}
	
	protected String getMenuId() {
		UIDynamicMenu menu = getMenu();
		if (menu != null) {
			return menu.getId();
		}
		return "";
	}
	
	@Override
	public String computeTemplate() {
		if (isEnabled()) {
			StringBuilder attributes = initAttributes();
			StringBuilder attrclasses = new StringBuilder();
			
			Iterator<UIComponent> it = getUIComponentList().iterator();
			while (it.hasNext()) {
				UIComponent component = (UIComponent)it.next();
				if (component instanceof UIAttribute) {
					UIAttribute uiAttribute = (UIAttribute)component;
					if (uiAttribute.getAttrName().equals("class")) {
						if (uiAttribute.isEnabled()) {
							attrclasses.append(attrclasses.length()>0 ? " ":"").append(uiAttribute.getAttrValue());
						}
					} else {
						attributes.append(component.computeTemplate());
					}
				}
			}
			
			String tagClass = getTagClass();
			if (attrclasses.indexOf(tagClass) == -1) {
				attrclasses.append(attrclasses.length()>0 ? " ":"").append(tagClass);
			}
			String attrclass = attrclasses.length()>0 ? " class=\""+ attrclasses +"\"":"";
			
			boolean pageIsEnabled = false;
			String pageName = getPageName();
			String pageIcon = "", pageIconPos = "";
			String pageTitle = "Please specify a page for item";
			if (!pageName.isEmpty()) {
				try {
					pageIsEnabled = getApplication().getPageComponentByName(pageName).isEnabled();
					pageTitle = getApplication().getPageComponentByName(pageName).getTitle();
					pageIcon = getApplication().getPageComponentByName(pageName).getIcon();
					pageIconPos = getApplication().getPageComponentByName(pageName).getIconPosition();
				} catch (Exception e) {}
			}
			
			String titleText = itemtitle;
			if (!titleText.isEmpty()) {
				titleText = "{{"+ titleText + "}}";
			}
			
			String title = titleText.isEmpty() ? pageTitle:titleText;
			String pos = itemiconPos.isEmpty() ? pageIconPos:itemiconPos;
			String icon = itemicon.isEmpty() ? pageIcon:itemicon;
			String menuId = getMenuId();
			
			StringBuilder sb = new StringBuilder();
			sb.append("<").append(getTagName()).append(attrclass)
				.append(" ion-item")
				.append(pageIsEnabled ? "":" disabled")
				.append(menuId.isEmpty() ? " menuClose":" menuClose=\""+menuId+"\"")
				.append(pageName.isEmpty() ? "":" (click)=\"openPageWithName('"+ pageName +"')\"")
			  	.append(attributes.length()>0 ? attributes:"").append(">")
			  	.append(System.getProperty("line.separator"))
			  	.append(icon.isEmpty() ? "":"<ion-icon name=\""+ icon+ "\" "+ pos + "></ion-icon>")
			  	.append(System.getProperty("line.separator"))
			  	.append(title)
			  	.append(System.getProperty("line.separator"))
				.append("</").append(getTagName()).append(">")
				.append(System.getProperty("line.separator"));
			
			return sb.toString();
		}
		return "";
	}

	@Override
	public String toString() {
		String pageName = getPageName();
		return super.toString() + ": " + (pageName.isEmpty() ? "?":pageName);
	}
	
	@Override
	public String[] getTagsForProperty(String propertyName) {
		if (propertyName.equals("itemicon")) {
			return EnumUtils.toStrings(IonIcon.class);
		}
		if (propertyName.equals("itemiconPos")) {
			return new String[] {"item-left","item-end","item-right","item-start"};
		}
		return new String[0];
	}
	
}
