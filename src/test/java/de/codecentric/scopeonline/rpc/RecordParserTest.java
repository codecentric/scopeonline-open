package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.RecordVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.SOAPBody;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RecordParserTest {
	private RecordParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new RecordParser();
	}


	@Test
	public void shouldConvertRecordNodeIfAccountIsValid() throws Exception {
		SOAPBody body = new MockRecordMessage( "1200" ).getBody();

		Set<String> validAccounts = new HashSet<>();
		validAccounts.add( "1200" );

		List<RecordVO> parsed = parser.parseRecords( body.getElementsByTagName( "record" ), validAccounts );

		assertEquals( 1, parsed.size() );
	}

	@Test
	public void shouldIgnoreRecordNodeThatIsNotFromAValidAccount() throws Exception {
		SOAPBody body = new MockRecordMessage( "1234" ).getBody();

		Set<String> validAccounts = new HashSet<>();
		validAccounts.add( "1200" );

		List<RecordVO> parsed = parser.parseRecords( body.getElementsByTagName( "record" ), validAccounts );

		assertEquals( 0, parsed.size() );
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}
}

