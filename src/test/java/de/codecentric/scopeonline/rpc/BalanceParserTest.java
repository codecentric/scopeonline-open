package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.BalanceVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BalanceParserTest {
	private BalanceParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new BalanceParser();
	}

	@Test
	public void shouldParseEachCSVLineWithCorrectAccountIntoBalances() throws Exception {
		SOAPElement csvElement = mockCsvElement();

		Set<String> accounts = new HashSet<>();
		accounts.add( "1000" );

		List<BalanceVO> balances = parser.parseAllBalancesForAccounts( csvElement, accounts,
																	   Calendar.getInstance() );
		assertEquals(0, new BigDecimal( 5.0 ).compareTo(  balances.get( 0 ).amount ));
		assertEquals(0, new BigDecimal( 35.0 ).compareTo(  balances.get( 1 ).amount ));
	}

	@Test
	public void shouldIgnoreEachCSVLineWithIncorrectAccount() throws Exception {
		SOAPElement csvElement = mockCsvElement();

		Set<String> accounts = new HashSet<>();
		accounts.add( "1100" );

		List<BalanceVO> balances = parser.parseAllBalancesForAccounts( csvElement, accounts,
																	   Calendar.getInstance() );
		assertEquals( 0, balances.size(), 0 );
	}

	private SOAPElement mockCsvElement() throws SOAPException {
		SOAPMessage message = MessageFactory.newInstance().createMessage();
		SOAPBody body = message.getSOAPBody();
		SOAPElement csvElement = body.addChildElement( "susa" );
		csvElement.setTextContent( "1000;one;two;three;four;5.0\n1000;one;two;three;four;35.0" );
		return csvElement;
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}
} 