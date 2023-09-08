package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	private static final String TEXT = "text";
	private static final String QUERY = "select E from TestEntity E where text=:text";

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh17188Test() throws Exception {
		// create entity
		EntityManager entityManager1 = entityManagerFactory.createEntityManager();
		entityManager1.getTransaction().begin();
		TestEntity entity1 = new TestEntity();
		entity1.setId(1L);
		entity1.setText(TEXT);
		entityManager1.persist(entity1);
		entityManager1.getTransaction().commit();
		entityManager1.close();

		// save query cache from managed entity
		EntityManager entityManager2 = entityManagerFactory.createEntityManager();
		entityManager2.find(TestEntity.class, 1L);
		TestEntity entity2 = entityManager2.createQuery(QUERY, TestEntity.class).setParameter("text", TEXT).setHint("org.hibernate.cacheable", "true").getSingleResult();
		Assert.assertEquals(TEXT, entity2.getText());
		entityManager2.close();

		// use query cache
		EntityManager entityManager3 = entityManagerFactory.createEntityManager();
		TestEntity entity3 = entityManager3.createQuery(QUERY, TestEntity.class).setParameter("text", TEXT).setHint("org.hibernate.cacheable", "true").getSingleResult();
		Assert.assertEquals(TEXT, entity3.getText()); // entity3.getText() is null
		entityManager3.close();
	}
}
