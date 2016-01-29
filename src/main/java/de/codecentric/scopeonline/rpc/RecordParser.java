package de.codecentric.scopeonline.rpc;

import de.codecentric.scopeonline.data.RecordVO;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class RecordParser {
	public List<RecordVO> parseRecords( final NodeList recordNodes, final Set<String> validAccounts ) {
		List<RecordVO> recordVOList = new ArrayList<>();
		int i = -1;
		while( ++i<recordNodes.getLength() ) {
			RecordVO recordVO = parseRecord( recordNodes.item( i ) );
			if( recordVO.account != null && validAccounts.contains( recordVO.account ) )
				recordVOList.add( recordVO );
		}
		return recordVOList;
	}

	private RecordVO parseRecord( final Node recordNode ) {
		RecordNode convertedNode = new RecordNode( recordNode.getChildNodes() );
		return convertedNode.toRecord();
	}
}

