package de.codecentric.scopeonline.persistence;

import de.codecentric.scopeonline.data.BalanceVO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class BalanceHashMapQueriesImpl implements BalanceHashMapQueries {
	EntityManagerFactory emf;

	public BalanceHashMapQueriesImpl( final EntityManagerFactory emf ) {
		this.emf = emf;
	}

	@Override
	public void insertBalanceHashMap( final HashMap<String, BalanceVO> balanceVOHashMap ) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for ( Entry<String, BalanceVO> entry : balanceVOHashMap.entrySet()){
			em.merge( entry.getValue() );
		}
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public HashMap<String, BalanceVO> getBalanceHashMap() {
		HashMap<String, BalanceVO> balanceVOHashMap = new HashMap<>();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List balanceVOList = em.createQuery( "select b from BalanceVO b" ).getResultList();
		em.close();
		for (Object object : balanceVOList)
		{
			BalanceVO balanceVO = (BalanceVO)object;
			balanceVOHashMap.put( balanceVO.dateAsString, balanceVO );
		}
		return balanceVOHashMap;
	}
}
