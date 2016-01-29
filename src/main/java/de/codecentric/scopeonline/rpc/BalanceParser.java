package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.BalanceVO;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

class BalanceParser {

	public List<BalanceVO> parseAllBalancesForAccounts( final Node csvNode,
														final Set<String> accounts,
														final Calendar date ) throws SOAPException {
		List<BalanceVO> balances = new ArrayList<>();
		String[] lines = csvNode.getTextContent().split( "\n" );
		for( String line : lines ) {
			BalanceCsvEntry entry = new BalanceCsvEntry( line );
			if( accounts.contains( entry.getAccountNo() ) )
				balances.add( new BalanceVO( entry.getAmount(), date ) );
		}
		return balances;
	}

}

