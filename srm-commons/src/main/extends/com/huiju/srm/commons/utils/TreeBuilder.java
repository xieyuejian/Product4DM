package com.huiju.srm.commons.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形构建
 * 
 * @author Administrator
 *
 */
public class TreeBuilder {

	/**
	 * 两层循环实现建树
	 * 
	 * @param treeNodes 传入的树节点列表
	 * @return
	 */
	public static List<TreeNode> bulid(List<TreeNode> treeNodes) {
		List<TreeNode> trees = new ArrayList<TreeNode>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode it : treeNodes) {
				if (it.getFatherCode() == treeNode.getNodeCode()) {
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<TreeNode>());
					}
					treeNode.getChildren().add(it);
				}
			}
			if ("-1".equals(treeNode.getFatherCode())) {
				trees.add(treeNode);
			}
		}
		return trees;
	}

	/**
	 * 使用递归方法建树形结构
	 * 
	 * @param treeNodes 传入的树节点列表
	 * @return
	 */
	public static List<TreeNode> buildByRecursive(List<TreeNode> treeNodes) {
		List<TreeNode> trees = new ArrayList<TreeNode>();
		for (TreeNode treeNode : treeNodes) {
			if ("-1".equals(treeNode.getFatherCode())) {
				trees.add(findChildren(treeNode, treeNodes));
			}
		}
		return trees;
	}

	/**
	 * 递归查找子节点
	 * 
	 * @param treeNodes 子节点列表
	 * @return
	 */
	public static TreeNode findChildren(TreeNode treeNode, List<TreeNode> treeNodes) {
		for (TreeNode it : treeNodes) {
			if (treeNode.getNodeCode().equals(it.getFatherCode())) {
				if (treeNode.getChildren() == null) {
					treeNode.setChildren(new ArrayList<TreeNode>());
				}
				treeNode.getChildren().add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}

}
