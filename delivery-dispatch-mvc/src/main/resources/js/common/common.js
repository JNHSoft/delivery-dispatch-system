/**
 * 네네치킨 관리자 공통스크립트
 */

//form 데이터를 JSON 형태로 바꾼다. 
(function($) {
	// jquery custom plugin
	$.fn.serializeObject = function() {
		try {
			var o = {};
			var a = this.serializeArray();
			$.each(a, function() {
				if (o[this.name]) {
					if (!o[this.name].push) {
						o[this.name] = [o[this.name]];
					}
					o[this.name].push(this.value || '');
				} else {
					o[this.name] = this.value || '';
				}
			});
			return o;
		} catch (e) {
			console.log(e);
			alert('시스템 오류입니다. 관리자에게 문의하세요.');
		}
	};
}(jQuery));

//공통 스크립트 (input text Validation check) 
var neneValidator = (function(nene) {
	
	nene.isRequired = function ($el) {
		if ($el.val() === '' && $el.data('valid-required') == true) {
			alert($el.data('valid-message') + '을(를) 입력하세요');
			return false;
		}
		return true;
	} 
	
	nene.isMaxLength = function ($el) {
		var max = $el.data('valid-maxlength');
		if (max !== '' && $el.val().length > max && $el.data('valid-required') == true) {
			alert($el.data('valid-message') + '는 최대' + max + '자리까지 입력가능합니다.');
			return false;
		}
		return true;
	}
	
	nene.isRegexNumber = function ($el) {
		var isNumber= /^[0-9]*$/;
		
		if($el.data('valid-regex') == true && $el.data('valid-required') == true && !isNumber.test($el.val())) {
			alert($el.data('valid-message') + '는 ' + '숫자만 입력이 가능합니다.' );
			return false;
		}
		return true;
	}
	
	return {
		isValid : function($form) {
			var isValid = true;
			$.each($form.find(':input[type=text]'), function(index, el) {
				isValid = nene.isRequired($(el)) && nene.isMaxLength($(el)) && nene.isRegexNumber($(el));
				return isValid;
			});
			return isValid;
		}
	};
}(window.neneValidator || {}))


var keyEvent = (function() {
	return {
		enter : function($target, _callback) {
			$target.unbind('keyup').bind('keyup', function(e) {
				if(e.keyCode != 13) {
					return;
				}
				if (typeof _callback === 'function') {
					_callback.call($target);
				}
			});
		}
	}
	
}(window.keyEvent || {}));

/**
 * keypressNumber()
 * - onkeypress 이벤트(숫자만입력)
 *
 * @return
 */
function keypressNumber() {
    if ((event.keyCode < 48) || (event.keyCode > 57)) event.returnValue = false;
}

/**
 * getNumberNDotKey()
 * - 숫자만 입력 가능 하도록  alert 문구
 *
 * @return
 */ 
function getNumberNDotKey() {
	 if ((event.keyCode >=48 && event.keyCode <=57)   // 자판 0~9
	  || (event.keyCode >=96 && event.keyCode <= 105)  // keypad 0~9
	  || (event.keyCode == 109)             // 자판 -
	  || (event.keyCode == 189)             // keypad -
	  || (event.keyCode == 110)             // 자판 .
	  || (event.keyCode == 190)             // keypad .
	  || (event.keyCode == 8)              // back space
	  || (event.keyCode == 9)              // tab
	  || (event.keyCode == 13)             // enter
	  || (event.keyCode == 46)             // delete
	  || (event.keyCode >= 37 && event.keyCode <= 40)) // 방향키
	 {
		 
	 } else {
		alert("숫자만 입력 가능합니다!");
		event.returnValue = false;
	 }
}


/** 
 * input 숫자와 콤마만 입력되게 하기.
 * include js : jquery.number.js
 * input 속성에 numberOnly 추가
 * jsp : <input type="text" id="amount" name="amount" numberOnly placeholder="0" />
 * $(this).number(true);
 * $.number( 5020.2364 );				// Outputs 5,020
 * $.number( 5020.2364, 2 );			// Outputs: 5,020.24
 * $.number( 135.8729, 3, ',' );		// Outputs: 135,873
 * $.number( 5020.2364, 1, ',', ' ' );	// Outputs: 5 020,2 
 */
$(document).on("keyup", "input:text[numberOnly]", function() {
	$(this).number(true);
});


 
/**
 * 전체 지사/법인/가맹점 조회
 * param : formId, id, 자사/법인/가맹점구분
 */
function getCompanyCodeList(param) {

	$("#" + param.formId + " #" + param.id).empty();

	$.ajax({
		url : "/common/api/getCompanyCodeList",
		method : "POST",
		success : function(data) {

			if(data.getCompanyCodeList.length > 0 ) {
				var companySize = data.getCompanyCodeList.length;
				var html = "";
				html = "<option value=''>전체</option>";

				for(var i=0; i<companySize; i++) {
					
					if(data.getCompanyCodeList[i].field_cd == param.field_cd) {
						html += "<option value='"+data.getCompanyCodeList[i].company_cd+"'>" + data.getCompanyCodeList[i].company_nm + "</option>";
					}
				}
				
				$("#" + param.formId + " #" + param.id).html(html);

			}
		}
	})
}

/**
 * 중분류/소분류 콤보 조회
 */
function getMsdClsCodeList(param) {

	$("#" + param.formId + " #" + param.resultId).empty();

	
	$.ajax({
		url : "/common/api/getMsdClsCodeList",
		method : "POST",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(param),
		success : function(data) {

			if(data.getMsdClsCodeList.length > 0 ) {
				var msdClsCode = data.getMsdClsCodeList.length;
				var html = "";
				
				if("ALL" == param.select) {
					html = "<option value=''>전체</option>";
				} else if("SELECT" == param.select) {
					html = "<option value=''>선택</option>";
				}

				for(var i=0; i<msdClsCode; i++) {
					
					if(data.getMsdClsCodeList[i].field_cd == param.field_cd) {
						if(data.getMsdClsCodeList[i].code_value == param.selectValue) {
							html += "<option value='"+data.getMsdClsCodeList[i].code_value+"' selected>" + data.getMsdClsCodeList[i].code_name+ "</option>";
						} else {
							html += "<option value='"+data.getMsdClsCodeList[i].code_value+"'>" + data.getMsdClsCodeList[i].code_name + "</option>";
						}
						
					}
				}
				
				$("#" + param.formId + " #" + param.resultId).html(html);

			}
		}
	})
}


var chkThousand = function(num) {
	num = String(num);
	num = num.replace(/,/g, '');	//콤마 제거
	var commaValue = '';
	
	for(var i=1; i<=num.length; i++) {
		if(i>1 && (i%3)==1)
			commaValue = num.charAt(num.length-i) + "," + commaValue;
		else
			commaValue = num.charAt(num.length-i) + commaValue;
	}
	return commaValue;
}