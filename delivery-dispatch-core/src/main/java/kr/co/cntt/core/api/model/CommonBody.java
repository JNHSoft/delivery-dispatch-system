package kr.co.cntt.core.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor

public class CommonBody<T> {
	private final String result;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T code;
	private Map<String, String> error_desc;
}
