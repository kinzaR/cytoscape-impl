package org.cytoscape.ding.impl.cyannotator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.ding.impl.cyannotator.annotations.DingAnnotation;
import org.cytoscape.view.presentation.annotations.Annotation;
import org.cytoscape.view.presentation.annotations.GroupAnnotation;
import org.cytoscape.view.presentation.annotations.ShapeAnnotation;
import org.junit.Test;

public class AnnotationTreeTest extends AnnotationTest {

	
	private static Set<DingAnnotation> asSet(Annotation...annotations) {
		Set<DingAnnotation> set = new HashSet<>();
		for(Annotation a : annotations) {
			set.add((DingAnnotation)a);
		}
		return set;
	}
	
	private static void assertCycle(boolean expected, Set<DingAnnotation> annotations) {
		boolean actual = AnnotationTree.containsCycle(annotations);
		assertEquals(expected, actual);
	}
	
	private GroupAnnotation createBalancedBinaryTree(int depth, Set<DingAnnotation> all) {
		if(depth == 1) {
			ShapeAnnotation shape1 = createShapeAnnotation();
			ShapeAnnotation shape2 = createShapeAnnotation();
			GroupAnnotation group1 = createGroupAnnotation();
			group1.addMember(shape1);
			group1.addMember(shape2);
			all.add((DingAnnotation)shape1);
			all.add((DingAnnotation)shape2);
			all.add((DingAnnotation)group1);
			return group1;
		} else {
			GroupAnnotation group1 = createBalancedBinaryTree(depth-1, all);
			GroupAnnotation group2 = createBalancedBinaryTree(depth-1, all);
			GroupAnnotation parent = createGroupAnnotation();
			parent.addMember(group1);
			parent.addMember(group2);
			all.add((DingAnnotation)parent);
			return parent;
		}
	}
	
	
	@Test
	public void testDetectNoCycle() {
		ShapeAnnotation shape1 = createShapeAnnotation();
		ShapeAnnotation shape2 = createShapeAnnotation();
		ShapeAnnotation shape3 = createShapeAnnotation();
		GroupAnnotation group1 = createGroupAnnotation();
		GroupAnnotation group2 = createGroupAnnotation();
		group1.addMember(shape1);
		group1.addMember(shape2);
		group2.addMember(group1);
		group2.addMember(shape3);
		
		assertCycle(false, asSet(shape1, shape2, shape3, group1, group2));
	}
	
	@Test
	public void testDetectNoCycleSimple() {
		ShapeAnnotation shape1 = createShapeAnnotation();
		GroupAnnotation group1 = createGroupAnnotation();
		
		assertCycle(false, asSet(shape1));
		assertCycle(false, asSet(group1));
	}
	
	@Test
	public void testDetectNoCycleMultipleComponents() {
		// create two separate trees
		ShapeAnnotation shape1a = createShapeAnnotation();
		ShapeAnnotation shape2a = createShapeAnnotation();
		ShapeAnnotation shape3a = createShapeAnnotation();
		GroupAnnotation group1a = createGroupAnnotation();
		GroupAnnotation group2a = createGroupAnnotation();
		group1a.addMember(shape1a);
		group1a.addMember(shape2a);
		group2a.addMember(group1a);
		group2a.addMember(shape3a);
		
		ShapeAnnotation shape1b = createShapeAnnotation();
		ShapeAnnotation shape2b = createShapeAnnotation();
		ShapeAnnotation shape3b = createShapeAnnotation();
		GroupAnnotation group1b = createGroupAnnotation();
		GroupAnnotation group2b = createGroupAnnotation();
		group1b.addMember(shape1b);
		group1b.addMember(shape2b);
		group2b.addMember(group1b);
		group2b.addMember(shape3b);
		
		Set<DingAnnotation> annotations = new HashSet<>();
		annotations.addAll(asSet(shape1a,  shape2a,  shape3a,  group1a,  group2a));
		annotations.addAll(asSet(shape1b,  shape2b,  shape3b,  group1b,  group2b));
		
		assertCycle(false, annotations);
	}
	
	@Test
	public void testDetectCycleSimple() {
		GroupAnnotation group1 = createGroupAnnotation();
		GroupAnnotation group2 = createGroupAnnotation();
		group1.addMember(group2);
		group2.addMember(group1);
		
		assertCycle(true, asSet(group1, group2));
	}
	
	@Test
	public void testDetectCycleSelfReference() {
		GroupAnnotation group1 = createGroupAnnotation();
		group1.addMember(group1);
		
		assertCycle(true, asSet(group1));
	}
	
	
	@Test
	public void testDetectCycleBig() {
		Set<DingAnnotation> all = new HashSet<>();
		GroupAnnotation g1 = createBalancedBinaryTree(5, all);
		GroupAnnotation g2 = createBalancedBinaryTree(5, all);
		GroupAnnotation g3 = createBalancedBinaryTree(5, all);
		GroupAnnotation g4 = createBalancedBinaryTree(5, all);
		GroupAnnotation g5 = createBalancedBinaryTree(5, all);
		GroupAnnotation g6 = createBalancedBinaryTree(5, all);
		g1.addMember(g2);
		g2.addMember(g3);
		g3.addMember(g4);
		g4.addMember(g5);
		g5.addMember(g6);
		g6.addMember(g1);
		
		assertEquals(378, all.size()); // sanity check
		assertCycle(true, all);
	}
	

	@Test
	public void testDetectNoCycleBig() {
		Set<DingAnnotation> all = new HashSet<>();
		GroupAnnotation g1 = createBalancedBinaryTree(5, all);
		GroupAnnotation g2 = createBalancedBinaryTree(5, all);
		GroupAnnotation g3 = createBalancedBinaryTree(5, all);
		GroupAnnotation g4 = createBalancedBinaryTree(5, all);
		GroupAnnotation g5 = createBalancedBinaryTree(5, all);
		GroupAnnotation g6 = createBalancedBinaryTree(5, all);
		g1.addMember(g2);
		g2.addMember(g3);
		g3.addMember(g4);
		g4.addMember(g5);
		g5.addMember(g6);
		// g6.addMember(g1);  // NO CYCLE
		
		assertEquals(378, all.size()); // sanity check
		assertCycle(false, all);
	}
	
	
	@Test
	public void testConvertToTree() {
		GroupAnnotation group2 = createGroupAnnotation("group2", 0);
		GroupAnnotation group1 = createGroupAnnotation("group1", 1);
		ShapeAnnotation shape1 = createShapeAnnotation("shape1", 2);
		ShapeAnnotation shape2 = createShapeAnnotation("shape2", 3);
		ShapeAnnotation shape3 = createShapeAnnotation("shape3", 4);
		group1.addMember(shape1);
		group1.addMember(shape2);
		group2.addMember(group1);
		group2.addMember(shape3);
		
		Set<DingAnnotation> annotations = asSet(shape1, shape2, shape3, group1, group2);
		AnnotationTree root = AnnotationTree.buildTree(annotations);
		
		// the root of the tree does not contain an annotation
		assertNull(root.getAnnotation());
		assertEquals(1, root.getChildCount());
		
		AnnotationTree ng2 = root.getChildren().get(0);
		assertEquals("group2", ng2.getAnnotation().getName());
		assertEquals(2, ng2.getChildCount());
		assertEquals("shape3", ng2.getChildren().get(1).getAnnotation().getName());
		AnnotationTree ng1 = ng2.getChildren().get(0);
		assertEquals("group1", ng1.getAnnotation().getName());
		assertEquals(2, ng1.getChildCount());
		assertEquals("shape1", ng1.getChildren().get(0).getAnnotation().getName());
		assertEquals("shape2", ng1.getChildren().get(1).getAnnotation().getName());
		
		List<Annotation> depthFirst = root.depthFirstOrder();
		assertEquals(5, depthFirst.size());
		assertEquals("group2", depthFirst.get(0).getName());
		assertEquals("group1", depthFirst.get(1).getName());
		assertEquals("shape1", depthFirst.get(2).getName());
		assertEquals("shape2", depthFirst.get(3).getName());
		assertEquals("shape3", depthFirst.get(4).getName());
		
		assertEquals(ng2, root.get(group2));
		assertEquals(ng1, root.get(group1));
		assertEquals(ng1.getChildren().get(0), ng1.get(shape1));
	}
	
	
	@Test
	public void testDetectDuplicateMembership() {
		GroupAnnotation group1 = createGroupAnnotation();
		GroupAnnotation group2 = createGroupAnnotation();
		ShapeAnnotation shape1 = createShapeAnnotation();
		group1.addMember(shape1);
		try {
			group2.addMember(shape1);
			fail();
		} catch(IllegalAnnotationStructureException e) {}
	}
	
	
	@Test
	public void testTreePath() {
		ShapeAnnotation shape1 = createShapeAnnotation();
		ShapeAnnotation shape2 = createShapeAnnotation();
		ShapeAnnotation shape3 = createShapeAnnotation();
		GroupAnnotation group1 = createGroupAnnotation();
		GroupAnnotation group2 = createGroupAnnotation();
		group1.addMember(shape1);
		group1.addMember(shape2);
		group2.addMember(group1);
		group2.addMember(shape3);
		
		Set<DingAnnotation> annotations = asSet(shape1, shape2, shape3, group1, group2);
		AnnotationTree root = AnnotationTree.buildTree(annotations);
		
		AnnotationTree shape1Node = root.get(shape1);
		AnnotationTree group1Node = root.get(group1);
		AnnotationTree group2Node = root.get(group2);
		
		AnnotationTree[] path = shape1Node.getPath();
		assertEquals(4, path.length);
		assertEquals(root, path[0]);
		assertEquals(group2Node, path[1]);
		assertEquals(group1Node, path[2]);
		assertEquals(shape1Node, path[3]);
	}

}
