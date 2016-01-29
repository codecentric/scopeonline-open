package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.RecordVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordNodeTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void shouldConvertRecordNodeToRecordVO() throws Exception {
		SOAPBody body = new MockRecordMessage( "1200" ).getBody();

		NodeList records = body.getElementsByTagName( "record" );

		RecordNode node = new RecordNode( records.item( 0 ).getChildNodes() );
		RecordVO vo = node.toRecord();
		assertEquals( 0, new BigDecimal( 10000 ).compareTo( vo.amount ) );
		assertEquals( getDateOnJanuary1Of1970(), vo.date.getTime() );
		assertEquals( "1200", vo.account );
	}

	@Test
	public void shouldConvertStringAmountToFloat() throws Exception {
		RecordNode node = new RecordNode( new BigDecimal( 0 ), new Date(), "0" );
		assertEquals( 0, new BigDecimal( 10000 ).compareTo( node.convertToBigDecimalAmount( "10.000,00" ) ) );
	}

	@Test
	public void shouldConvertStringDateToDate() throws Exception {
		Date date = getDateOnJanuary1Of1970();
		RecordNode node = new RecordNode( new BigDecimal( 0 ), new Date(), "0" );
		assertEquals( date, node.convertToDate( "01.01.1970" ) );
	}


	@Test
	public void shouldCatchParseExceptions() throws Exception {
		NodeList list = mock( NodeList.class );
		Node node = mock( Node.class );
		when( list.getLength() ).thenReturn( 1 );
		when( list.item( 0 ) ).thenReturn( node );

		when( node.getNodeName() ).thenReturn( "postingDate" );
		when( node.getTextContent() ).thenReturn( "asbsd" );
		RecordNode recordNode = new RecordNode( list );
		assertNotNull( recordNode );
	}

	private Date getDateOnJanuary1Of1970() {
		Date date = new Date();
		date.setTime( -60*60*1000 );
		return date;
	}

	@After
	public void tearDown() throws Exception {
	}
} 