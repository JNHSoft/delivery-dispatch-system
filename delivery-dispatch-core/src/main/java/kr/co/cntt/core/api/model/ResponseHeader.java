package kr.co.cntt.core.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * App api전용 response header
 * @author su
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHeader {
	private String token;
	private String date;
}
