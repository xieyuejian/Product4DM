package com.huiju.srm.commons.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 树形结构
 * 
 * @author Administrator
 *
 */
public class TreeNode implements Serializable {

	private static final long serialVersionUID = -3868065201639297010L;
	private String id;
	private String text;
	private String fatherCode;
	private String nodeCode;
	private Boolean leaf;
	private Boolean checked;
	private Boolean expanded;
	private List<TreeNode> children;
	private Map<String, Object> attr;

	public TreeNode() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getLeaf() {
		return this.leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public Boolean getChecked() {
		return this.checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Map<String, Object> getAttr() {
		return this.attr;
	}

	public void setAttr(Map<String, Object> attr) {
		this.attr = attr;
	}

	public String getFatherCode() {
		return fatherCode;
	}

	public void setFatherCode(String fatherCode) {
		this.fatherCode = fatherCode;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

}
