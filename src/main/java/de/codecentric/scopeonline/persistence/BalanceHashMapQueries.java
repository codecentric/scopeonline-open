package de.codecentric.scopeonline.persistence;

import de.codecentric.scopeonline.data.BalanceVO;

import java.util.HashMap;

public interface BalanceHashMapQueries {
	HashMap<String,BalanceVO> getBalanceHashMap();

	void insertBalanceHashMap( HashMap<String, BalanceVO> balanceVOHashMap );
}
