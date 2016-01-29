package de.codecentric.scopeonline.domain;

import de.codecentric.scopeonline.data.BalanceVO;
import de.codecentric.scopeonline.data.RecordVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccumulatorImpl implements Accumulator {
	private BalanceVO initialBalance;

	@Override
	public List<BalanceVO> accumulate( final List<RecordVO> values ) {
		return values != null && values.size()>0
			   ? calculateBalances( values )
			   : createEmptyBalance();
	}

	private List<BalanceVO> createEmptyBalance() {
		ArrayList<BalanceVO> balances = new ArrayList<>();
		BalanceVO balance = new BalanceVO( initialBalance.amount, initialBalance.date );
		balances.add( balance );
		return balances;
	}

	private List<BalanceVO> calculateBalances( final List<RecordVO> values ) {
		final ArrayList<BalanceVO> balances = new ArrayList<>();
		Collections.sort( values );
		BalanceVO balance = new BalanceVO( initialBalance.amount, values.get( 0 ).date );
		for( RecordVO record : values ) {
			if( !record.isOnSameDayAs( balance ) ) {
				balances.add( balance );
				balance = new BalanceVO( balance.amount, record.date );
			}
			balance.amount = balance.amount.add( record.amount );
		}
		balances.add( balance );

		return balances;
	}


	@Override
	public void setInitialBalance( final BalanceVO balanceVO ) {
		this.initialBalance = balanceVO;
	}

	public AccumulatorImpl( final BalanceVO initialBalance ) {
		this.initialBalance = initialBalance;
	}

	public AccumulatorImpl() {
		this.initialBalance = new BalanceVO();
	}

	@Override
	public BalanceVO getInitialBalance() {
		return initialBalance;
	}
}

