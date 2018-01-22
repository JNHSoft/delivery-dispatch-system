function($, payModule) {
	'use strict'
	var __typeArr = ['kcp'];
	var __ref = {};
	__ref.kcp = (function() {
		// kcp prototype
		var __kcp__ = function() {
			this.createProperties = function(ext) {
				return this._kcpProperties = $.extend({}, {
					req_tx : 'pay',
					res_cd : '',	
					tran_cd : '',	
					quotaopt : '12',
					buyr_tel1 : '',
					use_pay_method : '',	
					cash_yn : '',	
					cash_tr_code : '',	
					enc_info : '',	
					enc_data : ''}, ext);
			}
			this.createKcpForm = function(data) {
				var _kcpForm = document.getElementById('kcpForm');
				if (_kcpForm) {
					_kcpForm.remove();
				}
				_kcpForm = document.createElement('form');
				_kcpForm.id = "kcpForm";
				_kcpForm.name = "kcpForm";
				
				for (var key in data) {
					var _input = document.createElement('input');
					_input.type = "hidden";
					_input.name = key;
					_input.value = data[key];
					_kcpForm.appendChild(_input);
				}
				return _kcpForm;
			};
			this.doPay = function(){};	// 재정의하여 사용한다. 필수 method
			this.doComplete = function(){};	// 재정의하여 사용한다. 필수 method
		};
		// kcp constructor
		function Kcp() {
			this.name = 'kcp';
			// property initailize
			this.createProperties({
				pay_method : '100000000000',	// default : 카드 (100000000000)
				site_name : '',
				currency : 'WON',
				module_type : '01',	
				res_msg : '',
				ret_pay_method : '',	
				ordr_chk : '',	
				cash_id_info : '',	
				good_expr : '0'
			});
			// 결제 하기 버튼 클릭
			this.request = function(_props, _callback) {
				this._callback = _callback;
				// Payplus Plug-in 실행
				this._kcpForm = this.createKcpForm($.extend({}, this._kcpProperties, _props));
				if (!this._kcpForm) return false;
				KCP_Pay_Execute(this._kcpForm); 
			};
			// 인증 완료 시 콜백 함수 호출
			this.complete = function(FormOrJson, closeEvent) {
				if (typeof GetField === 'function') {
					GetField(this._kcpForm, FormOrJson);
				}
				if (this._kcpForm.res_cd.value == "0000") {
					document.body.appendChild(this._kcpForm);
					this._callback.call(this, $(this._kcpForm).serializeObject());
				} else {
					var res_cd = this._kcpForm.res_cd.value;
					var res_msg = this._kcpForm.res_msg.value;
					closeEvent();
				}
			};
		};
		// kcp mobile constructor
		function KcpMobile() {
			var __URL = '/kcp/paymentApproval';
			var __SUCCESS = '0000';
			this.name = 'kcpMobile';
			this.createProperties({// MOBILE
				ActionResult : 'card',
				currency : '410',
				approval_key : '',
				escw_used : 'N',
				van_code : '',
				ipgm_date : '',
				tablet_size : '1.0',
				PayUrl : '',
				traceNo : '',
				encoding_trans : 'UTF-8',
				Ret_URL : window.location.protocol + "//" + window.location.host  + "/kcp/mobilePaymentRequest"
			});
			
			// KCP 모바일 요청
			this.request = function(_props) {
				var data = $.extend({}, this._kcpProperties, _props);
				var _kcpForm = this.createKcpForm(data);
				$.ajax({
					type : 'post',
					contentType : 'application/json',
					dataType : 'json',
					url : __URL,
					data : JSON.stringify(data),
					// 전처리
					beforeSend : function() {
					},
					// 후처리 : 성공시
					success : function(response) {
						if (response.Code == __SUCCESS) {
							// 응답 SET 후 서브밋
							var PayUrl = response.PayUrl;
							_kcpForm.approval_key.value = response.approvalKey;
							_kcpForm.PayUrl.value = PayUrl;
							_kcpForm.traceNo.value = response.traceNo;
							_kcpForm.method = "post";
							_kcpForm.action = PayUrl.substring(0, PayUrl.lastIndexOf("/")) + "/jsp/encodingFilter/encodingFilter.jsp"; 
							document.body.appendChild(_kcpForm);
							_kcpForm.submit();
						} else {
							alert('시스템 오류입니다. 결제 요청에 실패 했습니다.');
							return;
						}
					},
					// 후처리 : 통신 오류시
					error : function(e) {
						alert('시스템 오류입니다. 결제 요청에 실패 했습니다.');
						return false;
					},
					// 후처리 : 통신완료시
					complete : function() {
					}
				});
			};
		};
		KcpMobile.prototype = Kcp.prototype = new __kcp__();
		
		function initialize() {
			try {
				if (!isMobile()) {
					try {
						kcpTx_install();
					} catch (e) {
						console.log('kcp 모듈 미설치');
						return;
					}
					this._instance = new Kcp();
				} else {
					window.self.name = "tar_opener";
					this._instance = new KcpMobile();
				}
			} catch(e) {
				console.log('시스템 오류입니다.');
			}
		}
		
		return {
			initialize : function() {
				try {
					initialize.call(this);
				} catch (e) {
					alert('결제모듈 초기화 실패');
				}
			},
			doPay : function(_props, _callback) {
				if (!isMobile() && (typeof _callback === undefined || typeof _callback !== 'function')) {
					alert('결제 후처리 프로세스가 정의되지 않았습니다.');
					return;
				}
				try {
					this._instance.request(_props, _callback);
				} catch (e) {
					//alert('결제모듈 실행중 오류 발생.');
				}
				return false;
			},
			doComplete : function() {
				try {
					var formorjson = arguments[0];
					var closeEvent = arguments[1];
					this._instance.complete(formorjson, closeEvent);
				} catch (e) {
					//alert('결제모듈 실행중 오류 발생.');
				}
			}
		};
	}());
	
	// private
	function isMobile() {
		var regMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i;
		if (regMobile.test(window.navigator.userAgent)) {
			return true;
		}
		return false;
	};
	
	function initializer(arr) {
		var module = {};
		for (var i=0, l=arr.length; i < l; i++) {
			var type = arr[i];
			if (type.indexOf(__typeArr) === -1) {
				alert('지원하지 않는 모듈이 포합되어 있습니다.');
				continue;
			}
			var ref = __ref[type];
			if (ref) {
				module[type] = ref;
				if (typeof ref.initialize === 'function') {
					ref.initialize();
				}
			}
		}
		return module;
	};
	
	// public
	payModule.apply = function(typeArr) {
		return $.extend(this, initializer.call(this, typeArr));
	};
	
	payModule.pay = function(_type, _props, _callback) {
		if (_type.indexOf(__typeArr) === -1) return alert('지원하지 않는 결제모듈입니다.');
		if (!_props) {
			alert('시스템 오류입니다. 폼생성에 실패하였습니다.');
			return;
		} 
		this[_type].doPay(_props, _callback);
	};
	
	payModule.payComplete = function() {
		var args = [].slice.call(arguments);
		var _type = args.shift();
		if (_type && typeof this[_type].doComplete === 'function') {
			this[_type].doComplete.apply(this[_type], args);
			return false;
		}
		alert('결제처리가 정상적으로 완료되지 않았습니다.');
	}
	return window.payModule = payModule;
}(jQuery, window.payModule || {}));

(function() {
	Array.prototype.indexOf || (Array.prototype.indexOf = function(a, b) {
		for (var c = b || 0, d = this.length; d > c; c++)
			if (this[c] === a)
				return c;
		return -1
	})
});