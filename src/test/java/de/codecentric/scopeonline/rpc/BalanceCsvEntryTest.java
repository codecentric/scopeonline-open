package de.codecentric.scopeonline.rpc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BalanceCsvEntryTest {
	private BalanceCsvEntry entry;

	@Before
	public void setUp() throws Exception {
		entry = new BalanceCsvEntry( "1000;one;two;three;four;5.0" );
	}

	@Test
	public void shouldReturnFirstFieldAsAccountNumber() throws Exception {
		assertEquals( "1000", entry.getAccountNo() );
	}

	@Test
	public void shouldReturnFifthFieldAsAmount() throws Exception {
		assertEquals( 0, new BigDecimal( 5.0 ).compareTo( entry.getAmount() ) );
	}

	@After
	public void tearDown() throws Exception {
		entry = null;
	}
} 