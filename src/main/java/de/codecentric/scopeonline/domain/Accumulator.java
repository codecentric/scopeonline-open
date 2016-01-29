package de.codecentric.scopeonline.domain;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;

import java.util.List;

public interface Accumulator {
	List<BalanceVO> accumulate( List<RecordVO> values );

	void setInitialBalance( BalanceVO balanceVO );

	BalanceVO getInitialBalance();
}
