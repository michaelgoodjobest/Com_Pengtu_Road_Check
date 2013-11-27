package com.road.check.app.functionmodule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.widget.ListView;

import com.road.check.R;
import com.road.check.model.Node;
import com.road.check.model.NodeResource;

public class TreeListView extends ListView {
	ListView treelist = null;
	TreeAdapter ta = null;
	List<Node> nodelistmap;
	public TreeListView(Activity activity,List<NodeResource> res,int expandLevel) {
		super(activity);
		treelist = this;
		treelist.setFocusable(false);
		treelist.setBackgroundColor(0xffffff);
		treelist.setFadingEdgeLength(0);
		treelist.setLayoutParams(new LayoutParams(
				ListView.LayoutParams.FILL_PARENT,
				ListView.LayoutParams.WRAP_CONTENT));
		treelist.setDrawSelectorOnTop(false);
		treelist.setCacheColorHint(0x00000000);
		treelist.setDivider(getResources().getDrawable(R.drawable.list_halving_line));
		treelist.setDividerHeight(2);
		treelist.setFastScrollEnabled(true);
		treelist.setScrollBarStyle(NO_ID);
		
		initNode(activity, initNodRoot(res), true, -1, -1, expandLevel);
	}

	/**
	 * 
	 * @param context
	 *            响应监听的上下文
	 * @param root
	 *            已经挂好树的根节点
	 * @param hasCheckBox
	 *            是否整个树有复选框
	 * @param tree_ex_id
	 *            展开iconid -1会使用默认的
	 * @param tree_ec_id
	 *            收缩iconid -1会使用默认的
	 * @param expandLevel
	 *            初始展开等级
	 * 
	 */
	public List<Node> initNodRoot(List<NodeResource> res) {
		ArrayList<Node> list = new ArrayList<Node>();
		ArrayList<Node> roots = new ArrayList<Node>();
		Map<String, Node> nodemap = new HashMap<String, Node>();
		for (int i = 0; i < res.size(); i++) {
			NodeResource nr = res.get(i);
			Node n = new Node(nr.title, nr.value, nr.parentId, nr.curId,
					nr.iconId);
			nodemap.put(n.getCurId(), n);// 生成map树
		}
		Set<String> set = nodemap.keySet();
		Collection<Node> collections = nodemap.values();
		Iterator<Node> iterator = collections.iterator();
		while (iterator.hasNext()) {// 添加所有根节点到root中
			Node n = iterator.next();
			if (!set.contains(n.getParentId()))
				roots.add(n);
			list.add(n);
		}
		for (int i = 0; i < list.size(); i++) {
			Node n = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				Node m = list.get(j);
				if (m.getParentId() == n.getCurId()) {
					n.addNode(m);
					m.setParent(n);
				} else if (m.getCurId() == n.getParentId()) {
					m.addNode(n);
					n.setParent(m);
				}
			}
		}
		return roots;
	}

	public void initNode(Activity activity, List<Node> root, boolean hasCheckBox,
			int tree_ex_id, int tree_ec_id, int expandLevel) {
		nodelistmap = root;
		ta = new TreeAdapter(activity, root);
		// 设置整个树是否显示复选框
		ta.setCheckBox(false);
		// 设置展开和折叠时图标
		// ta.setCollapseAndExpandIcon(R.drawable.tree_ex, R.drawable.tree_ec);
		int tree_ex_id_ = (tree_ex_id == -1) ? R.drawable.tree_ex : tree_ex_id;
		int tree_ec_id_ = (tree_ec_id == -1) ? R.drawable.tree_ec : tree_ec_id;
		ta.setCollapseAndExpandIcon(tree_ex_id_, tree_ec_id_);
		// 设置默认展开级别
		ta.setExpandLevel(expandLevel);
		this.setAdapter(ta);
	}

	/* 返回当前所有选中节点的List数组 */
	public List<Node> get() {
		return ta.getSelectedNode();
	}

}
