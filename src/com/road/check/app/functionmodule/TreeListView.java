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
	 *            ��Ӧ������������
	 * @param root
	 *            �Ѿ��Һ����ĸ��ڵ�
	 * @param hasCheckBox
	 *            �Ƿ��������и�ѡ��
	 * @param tree_ex_id
	 *            չ��iconid -1��ʹ��Ĭ�ϵ�
	 * @param tree_ec_id
	 *            ����iconid -1��ʹ��Ĭ�ϵ�
	 * @param expandLevel
	 *            ��ʼչ���ȼ�
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
			nodemap.put(n.getCurId(), n);// ����map��
		}
		Set<String> set = nodemap.keySet();
		Collection<Node> collections = nodemap.values();
		Iterator<Node> iterator = collections.iterator();
		while (iterator.hasNext()) {// ������и��ڵ㵽root��
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
		// �����������Ƿ���ʾ��ѡ��
		ta.setCheckBox(false);
		// ����չ�����۵�ʱͼ��
		// ta.setCollapseAndExpandIcon(R.drawable.tree_ex, R.drawable.tree_ec);
		int tree_ex_id_ = (tree_ex_id == -1) ? R.drawable.tree_ex : tree_ex_id;
		int tree_ec_id_ = (tree_ec_id == -1) ? R.drawable.tree_ec : tree_ec_id;
		ta.setCollapseAndExpandIcon(tree_ex_id_, tree_ec_id_);
		// ����Ĭ��չ������
		ta.setExpandLevel(expandLevel);
		this.setAdapter(ta);
	}

	/* ���ص�ǰ����ѡ�нڵ��List���� */
	public List<Node> get() {
		return ta.getSelectedNode();
	}

}
