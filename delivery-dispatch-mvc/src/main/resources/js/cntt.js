/**
 * CNTT Develoment Common javascript
 */
var cntt = {};
/**
 * ajax
 */
cntt.ajax = (function(ajax) {
	// defaultOption
	var defaultOption = {
		type : 'post',
		cache : false,
		contentType : 'application/json',
		dataType : 'json',
		executeExceptionCallback : false,
		hasProgress : true
	};
	
	// common ajax module
	function ajax(option, callback) {
		$.ajax($.extend(option, {
			// 전처리
			beforeSend : function() {
				if (option.hasProgress && typeof loadingShow !== 'undefined' && typeof loadingShow === 'function') {
					loadingShow();
				}
			},
			// 후처리 : 성공시
			success : function(response) {
				if (typeof callback === 'function') {
					callback.call(this, response);
				}
			},
			// 후처리 : 통신 오류시
			error : function(e) {
				if (option.dataType === 'jsonp') return false; 
				var response = e.responseJSON ? e.responseJSON : null;
				if (response && response.fieldErrors && !option.executeExceptionCallback) {
					cntt.error.errorMessage(response.fieldErrors);
					return;
				}
				if (!option.executeExceptionCallback) {
					// 서버오류입니다.
					popAlert(response && response.errorMessage ? response.errorMessage : '잠시 후 재시도 해주시기 바랍니다.[1]');
					if (response && response.redirectUrl) {
						window.location.href = response.redirectUrl;
					}
					return false;
				}
				response.isError = true;
				callback.call(this, response);
			},
			// 후처리 : 통신완료시
			complete : function() {
				if (option.hasProgress && typeof loadingHide !== 'undefined' && typeof loadingHide === 'function') {
					loadingHide();
				}
			}
		}));
	}
	// get method
	function get(url, callback, options) {
		var option = $.extend({}, defaultOption, options);
		option.type = 'get';
		option.url = url;
		option.data = null;
		ajax(option, callback);
	}
	
	// post method
	function post(url, data, callback, options) {
		var option = $.extend({}, defaultOption, options);
		option.type = 'post';
		option.url = url;
		option.data = JSON.stringify(data);
		ajax(option, callback);
	}
	
	function formData(url, data, callback, options) {
		var option = $.extend({}, defaultOption, options);
		option.type = 'post';
		option.contentType = 'application/x-www-form-urlencoded; charset=UTF-8';
		option.url = url;
		option.data = data;
		ajax(option, callback);
	}
	return {
		get : get,
		post : post,
		formData : formData
	};
	
}(cntt.ajax || {}));
/**
 * 에러 메세지
 */
cntt.error = (function() {
	// alertTip
	function errorMessage(fieldErrors) {
		var $delay = 1000;
		var $speed = 300;
		for (var i=0; i < fieldErrors.length; i++) {
			try {
				var regExp = /\./gi;
				var target = fieldErrors[i].field;
				if (regExp.test(target)) {
					var tArr = target.split(regExp);
					if (tArr > 0) {
						target = tArr[tArr.length-1];
					}
				}
				var el = document.getElementById(target);
				if (el) {
					var $this = $(el);
					$left = $this.offset().left,
					$top = $this.offset().top,
					$height = $this.outerHeight(),
					$tip = $('<div class="alert_tip" style="left:'+$left+'px;top:'+($top+$height)+'px;">'+fieldErrors[i].message+'</div>');
					$this.addClass('error');
					$('body').append($tip);
				}
			} catch(e) {
				continue;
			}
		}
		$('.alert_tip').fadeIn($speed).delay($delay).fadeOut($speed,function(){		
			$('.alert_tip').remove();
		});
		$('.error').delay($delay).removeClass('error').eq(0).focus();
	}
	return {
		errorMessage : errorMessage
	}
}(cntt.error || {}));



/**
 * cookie
 */
cntt.cookie = (function(cookie) {
	// cookie set
	function set(name, value, expiredays) {
		var exdate = new Date();
		exdate.setDate(exdate.getDate() + expiredays);
		
		var cookie = null;
		cookie = document.cookie;
		cookie = name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString());
		cookie.Path = "/";
		document.cookie = cookie;
	}
	// cookie get
	function get(name) {
		var results = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
		if (results) {
		    return (unescape(results[2]));
		}
	    return null;
	}
	// cookie delete
	function del(name) {
		var cookieDate = new Date ();  // current date and time
		cookieDate.setTime(cookieDate.getTime() - 1);
		document.cookie = name += "=; expires=" + cookieDate.toGMTString();
	}
	
	return {
		set : set,
		get : get,
		del : del
	};
}(cntt.cookie || {}));

cntt.file = (function(f) {
	f.preView = function(obj) {
		// 지원브라우져 체크
		if (window.File && window.FileReader) {
			// 선택된 파일이 있는지 체크
			if (obj.files && obj.files.length > 0) {
				// 이미지 파일여부체크
				if (!(/image/gi).test(obj.files[0].type)){
					popAlert("미리보기 할 수 없는 형식의 파일입니다. image 파일만 미리보기가 가능합니다.");
					return false;
				}
				var reader = new FileReader();
				reader.onload = (function (o) {
					return function(e) {
						var $div = $(o).closest('.img');
						if ($div) {
							var $target = $div.find('img');
							if ($target.length === 0) {
								$target = $('<img id="preViewImage"/>');
								$div.find('span').remove();
								$div.prepend($target);
								
							}
							$target.attr('src', e.target.result);
						}
					};
				}(obj));
				reader.readAsDataURL(obj.files[0]);
			}
		} else {
			var $div = $(obj).closest('.img');
			if ($div) {
				$div.find('span').html('미리보기를 지원하지 <br/> 않는 브라우져입니다');
			}
		}
	};
	return f;
}(window.cntt.file || {}));

// 일반 페이지 이동 
cntt.goPage = function(url, param) {
	for (key in param) {
		cntt.localStorage.setItem(key, param[key]);
	}
	window.location.href = url;
};

// POST 페이지 이동
cntt.goPagePost = function(url, param) {
	var body = document.getElementsByTagName('body')[0];
	if (!body) return;
	var form = document.getElementById('pageForm');
	if (!form) {
		form = document.createElement('form');
	}
	form.id = "pageForm";
	form.name = "pageForm";
	form.method = "post";
	form.action = url;
	form.innerHTML = '';
	for (key in param) {
		var input = document.createElement('input');
		input.name = key;
		input.value = param[key];
		form.appendChild(input);
	}
	body.appendChild(form);
	form.submit();
	body.removeChild(form);
};

cntt.localStorage = {};

// 브라우저 로컬 스토리지에서 값 불러오기
cntt.localStorage.getItem = function(key, isNotRemove) {
	// isNotRemove == 꺼낼때 false면 삭제 O, true면 삭제 X
	var value = window.localStorage.getItem(key);
	if (!isNotRemove) {
		window.localStorage.removeItem(key);
	}
	return value;
};

// 브라우저 로컬 스토리지에 저장 
cntt.localStorage.setItem = function(key, value) {
	window.localStorage.setItem(key, value);
};

//multipart submit
cntt.multipart = {};

cntt.multipart.submit = function($form, callback, option) {
	var options = $.extend({
		//progressUrl : cnttGlobal.progressUrl,
		// progressbar 여부
		async : false,
		dataType : 'json',
		hasProgress : true,
		executeExceptionCallback : false,
		// 전처리
		beforeSubmit: function (data, form, o) {
			
			if (o.hasProgress && typeof loadingShow !== 'undefined' && typeof loadingShow === 'function') {
				loadingShow();
			}
			//validation체크 
			return true;
		}
		// 성공시
		,success: function(response, status) {
			if(typeof response === 'string'){
				response = response.replace(/[<][^>]*[>]/gi, '');
				response = JSON.parse(response);
			}
			// 전문통신 응답 오류 or 일반 user exception 발생 시
			response.error = response.errorCode ? true : false;
			// callback
			callback.call(this, response);
		}
		// 오류시
		,error: function(e){
			var response = e.responseJSON ? e.responseJSON : null;
			if (response && response.fieldErrors && !options.executeExceptionCallback) {
				cntt.error.errorMessage(response.fieldErrors);
				return;
			}
			if (!options.executeExceptionCallback) {
				// 서버오류입니다.
				popAlert(response && response.errorMessage ? response.errorMessage : '잠시 후 재시도 해주시기 바랍니다.[1]');
				return false;
			}
			response.isError = true;
			callback.call(this, response);
		}
		// 통신완료 후처리
		,complete : function() {
			if (options.hasProgress && typeof loadingHide !== 'undefined' && typeof loadingHide === 'function') {
				loadingHide();
			}
		}
    }, option || {});
	// jquery ajaxform
	$form.ajaxForm(options);
	$form.submit();
};

// jquery custom plugin
(function($) {
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
			popAlert('시스템 오류입니다. 관리자에게 문의하세요.');
		}
	};
	$.fn.getLoad = function(action, onSuccess, onError, onComplete) {
		onSuccess = onSuccess || function() {
		};
		onError = onError || function() {
		};
		onComplete = onComplete || function() {
		};
		
		var qArr = action.split(/\?/g);
		var uri = qArr[0];
		uri += '?frag=' + this.attr('id') + (qArr.length > 1 ? '&' + qArr[1] : '');
		this.load(uri, function(response, status, xhr) {
			if (status === 'error') {
				if (typeof alertTip === 'function') {
					popAlert('시스템 오류입니다');
				}
				onError(this, response, status, xhr);
				//location.href = '/error/error';
				
			} else {
				onSuccess(this, response);
			}
			/*
			if (status === 'error') {
				response = JSON.parse(response);
				response.isError = true;
			}
			*/
			
			onComplete(this, response);
		},{cache:false});
	};
	$.fn.postLoad = function(action, data, onSuccess, onError, onComplete) {
		onSuccess = onSuccess || function() {
		};
		onError = onError || function() {
		};
		onComplete = onComplete || function() {
		};
		
		if (typeof data !== 'object') return false;
		
		this.load(action, $.extend({}, data, {frag : this.attr('id')}), function(response, status, xhr) {
			if (status === 'error') {
				if (typeof alertTip === 'function') {
					popAlert('시스템 오류입니다');
				}
				onError(this, response, status, xhr);
				//location.href = '/error/error';
				
			} else {
				onSuccess(this, response);
			}

			onComplete(this, response);
		});
	};
	$.fn.multipartSubmit = function() {
		// TODO : 구현
	};
	$.fn.setFilePreview = function() {
		return $.each(this, function() {
			// type check
			if ($(this).prop('tagName').toLowerCase() !== 'input' || $(this).prop('type').toLowerCase() !== 'file') return true;
			$(this).unbind('change').bind('change', function() {
				cntt.file.preView(this);
			});
		});
	};
	$.fn.setData = function(ds) {
		return $.each(this, (function(_ds) {
			return function() {
				//$(this).attr('data-dataset', _ds);
				if (typeof _ds !== 'object') return;
				$(this).find('[data-col]').each(function(i){
					var k = $(this).data('col');
					if (!k || !_ds[k]) return true;
					$(this).val(_ds[k]);
				});
			}
		}(ds)));
	};
	$.fn.getData = function(ds) {
		var _ds = ds || {};
		if (typeof _ds !== 'object') return;
		$(this).find('[data-col]').each(function(i){
			var k = $(this).data('col');
			_ds[k] = $(this).val();
		});
		return _ds;
	};
}($));

/***************************************************************************************************************************************************
 * Native 객체 prototype 재정의
 ***************************************************************************************************************************************************/
//String.lpad()
String.prototype.lpad = function(len, char) {
	var value = ''+this;
	for(var i = value.length; i < len; i++){
		value = char + value;
	}
	return value;
};

//Number.lpad()
Number.prototype.lpad = function(len, char) {
	var value = ''+this;
	for(var i = value.length; i < len; i++){
		value = char + value;
	}
	return value;
};

String.prototype.splitTelno = function() {
	var regExp = /^(\d{2,3})(\d{3,4})(\d{4})$/;
	if(regExp.test(this)) {
		var telArr = regExp.exec(this);
		telArr.shift();
		return telArr;
	}
	return this;
};

String.prototype.telFormat = function() {
	var regExp = /^(\d{2,3})(\d{3,4})(\d{4})$/;
	if(regExp.test(this)) {
		return this.replace(regExp, '$1-$2-$3');
	}
	return this;
};
/***************************************************************************************************************************************************
 * window unload event bind
 ***************************************************************************************************************************************************/
//window event regiter
(function(w) {
	var clearCnttObject = function() {
		var targetArr = ['PageParam', 'PageFunction', 'HeaderParam', 'HeaderFunc'];
		for (var i=0, l=targetArr.length; i < l; i++) {
			try {
				var o = eval(targetArr[i]);
				if (eval(targetArr[i])) {
					o = null;
				}
			} catch(e) {
				// undefiend object ignored
			}
		}
	}
	var clearStorage = function() {
		try {
			if (window.localStorage) {
				window.localStorage.clear();
			}
			if (window.sessionStorage) {
				window.sessionStorage.clear();
			}
		} catch(e) {
			// undefiend storage ignored
		}
	}
	if (w) {
		// window unload
		if (w.attachEvent) {
			w.attachEvent('unload', function() {
				clearCnttObject.call(this);
				//clearStorage.call(this);
			});
		} else {
			w.addEventListener('unload', function() {
				clearCnttObject.call(this);
				//clearStorage.call(this);
			}, false);
		}
	}
	
	$(document).ready(function() {
		if (typeof HeaderFunc != 'undefined' && typeof HeaderFunc.init === 'function') {
			HeaderFunc.init.call(HeaderFunc);
		}
		if (typeof PageFunction != 'undefined' && typeof PageFunction.init === 'function') {
			PageFunction.init.call(PageFunction);
		}
	});
}(window));
