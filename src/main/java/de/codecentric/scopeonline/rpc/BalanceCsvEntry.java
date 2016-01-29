package de.codecentric.scopeonline.rpc;

import java.math.BigDecimal;

class BalanceCsvEntry {
	private final String[] fields;

	public String getAccountNo() {
		return fields[0];
	}

	public BigDecimal getAmount() {
		return new BigDecimal( Double.parseDouble( fields[5] ) );
	}

	BalanceCsvEntry( String line ) {
		fields = line.split( ";" );
	}
}
