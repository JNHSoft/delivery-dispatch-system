package kr.co.cntt.core.model.web;

import kr.co.cntt.core.model.AbstractPagination;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

/**
 * BOARD
 * @author Merlin
 *
 */
@Getter
@Setter
@Alias("board")
public class Board extends AbstractPagination{

	private String created_datetime;
	private String modified_datetime;
	private String id;
	private String branch_id;
	private String title;
	private String content;
	private String to_rider;
	private String to_store;
	private String to_branch;
	private String deleted;
}
