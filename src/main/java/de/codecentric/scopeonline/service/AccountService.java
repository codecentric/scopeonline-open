package de.codecentric.scopeonline.service;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;

import java.util.Calendar;
import java.util.List;

public interface AccountService {
	List<RecordVO> getAllRecords( Calendar startDate, Calendar endDate, String organization );

	List<BalanceVO> getBalancesForDate( Calendar date, String organization );
}
