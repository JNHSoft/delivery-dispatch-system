
$(function(){
	commonUi();
	cartBtn();
	formUi();
	tableUI();
	topBtn();	
	tabmenuUi();
	popupUI();
	snsCtrl();
	menuCtrl();
	etcCtrl();

	//placeholder
	$(":input[placeholder]").placeholder();

});


/* common */
function commonUi(){
	var lnbTxt = $('#lnb a');
	var gnbTxt = $('#gnb a');
	var lnbOn = $.trim($('#location > li > strong').text());
	var gnbOnTxt
	if($('#lnb h1 img').size() > 0){
		gnbOnTxt = $('#lnb h1 img').attr('alt');
	}else{
		gnbOnTxt = $('#lnb h1').text();
	}
	var gnbOn = $.trim(gnbOnTxt);

	if($('#location').size() > 0){
		document.title = lnbOn + ' | ' + '버거킹 딜리버리';
	}
	
	$(window).load(function(){
		gnbTxt.each(function() {
			 if ( $(this).text() == gnbOn) {
				$(this).parent().addClass('active');
			}
		});
		
		if($('#lnb').size() > 0){
			lnbTxt.each(function() {
				 if ( $(this).text() == lnbOn) {
					$(this).parents('li').addClass('active');
				}
			});
		} 
		
		if($('#lnb').size() > 0){
			var tip = $('#lnb').offset().top;
			$(window).scroll(function(){
				var top = $(this).scrollTop();
				if( top > (tip-10)){
					//$('#lnb').css({'position':'absolute','top':(top - 125)})
					$('#lnb').addClass('fixed');
				}else{
					//$('#lnb').removeAttr('style');
					$('#lnb').removeClass('fixed');
				}
			});		
		}
	})
	
	$(document).on('click','.lnb_open',function(){
		$('.open_view').show(500);
		return false;
	});

	$(document).on('click','.lnb_close',function(){
		$('.open_view').hide(500,function(){
			$('.lnb_open').focus();
		});
		return false;
	});
	
	$(document).on('click','.btn_gnb a',function(e){
		e.preventDefault();
		$('body').toggleClass('gnb_open');
		if($('.cart_view').is(':visible')){
			$('.cart_view').hide(500);
		}
	})
	$(document).on('click','.gnb_bg',function(){
		$('body').removeClass('gnb_open');
	})
	$(document).on('click','#gnb .in_sub',function(e){
		if($(window).width() < 741){
			$(this).next('ul').stop().slideToggle().closest('li').toggleClass('on').siblings().removeClass('on').find('ul').slideUp();
			e.preventDefault();
		}
	});
	$(window).resize(function(){
		$('#gnb > ul > li').removeClass('on').find('ul').removeAttr('style');
	})
	
	if(isMobile.any()){
	    if(isMobile.Android() || isMobile.IOS()){
	    	$('.head_call img').wrap('<a href="tel:1599-0505"></a>')
	    }
	}
}


/*장바구니*/
function cartBtn(){
	$(document).on("click",'.cart_btn', function(){
		$('.cart_view').show(500);
		return false;
	});

	$(document).on("click", '.btn_cartClose', function(){
		$('.cart_view').hide(500);
		return false;
	});
	
	$('.cart_box').on("click",'.cart_list > a', function(){
		var $this = $(this),
			$speed = 300;
		
		$this.slideUp($speed).next().slideDown($speed).parent().siblings().children('a').slideDown($speed).next().slideUp($speed,function(){
			$this.parent().addClass('on').siblings().removeClass('on');
		})
		
		return false;
	});
}

/*폼요소*/
function formUi(){
	//checkbox, radio
	$('.checkbox input, .radio input').focus(function(){
		$(this).parent().addClass('on');
	}).blur(function(){
		$(this).parent().removeClass('on');
	});

	//datepicker
	if($('.datepicker').length > 0){
		$( '.datepicker' ).datepicker({
			closeText: '닫기',
			prevText: '이전달',
			nextText: '다음달',
			currentText: '오늘',
			monthNames: ['01','02','03','04','05','06','07','08','09','10','11','12'],
			dayNamesMin: ['일','월','화','수','목','금','토'],
			dateFormat: 'yy-mm-dd',
			showMonthAfterYear: true,
			showOn: 'both',
			buttonText: '기간조회'
		});
	}

	//inp_file
	$('.inp_file > input').focus(function(){
		$(this).closest('.inp_file').find('.btn_file input').trigger('click');
	});	
	$('.inp_file .btn_file .button').click(function(e){
		e.preventDefault();
		$(this).closest('.inp_file').find('.btn_file input').trigger('click');
	});	
	$('.inp_file .btn_file input').change(function(){
		$(this).closest('.inp_file').find('> input').val($(this).val());
	});


	if($('.inp_spinner').length > 0){
		$('.inp_spinner').each(function(){
			var $this = $(this),
				$min = $this.data('min'),
				$max = $this.data('max'),
				$input = $this.find('.input'),
				$inputVal = $input.val(),
				$btn = $this.find('.btn');
				
			$input.after('<select class="select" title="수량선택"><option value="0">직접입력</option></select>');
			var $select = $this.find('.select');
			
			//console.log($inputVal)			
			
			//세팅
			for(i = $min;i <= $max;i++){
				$select.append($('<option>',{value: i,text : i}));
			}
			if($inputVal == '' ||$inputVal == null){
				$input.val($min)
				$select.val($min);
			}

			//셀렉트
			$select.change(function(){
				var $val = $(this).val();
				if($val == '0'){
					$select.addClass('hide');
					$input.addClass('on').attr("readonly",false).focus();
				}else{
					$input.val($val);
				}
			})
			
			//숫자 입력시
			$input.change(function(){
				var $val = $(this).val();
				if($min <= $val && $val <= $max){
					$select.val($val).removeClass('hide');
					$input.removeClass('on').attr("readonly",true);
				}else{
					alert($min+'에서 '+$max+'까지만 입력 가능합니다.\n다시 입력해주세요')
					$input.val($min);
					$select.val($min);
				}
			})
			var $firstVal
			$input.focusin(function(){
				$firstVal = $(this).val();
			})
			$input.focusout(function(){
				var $lastVal = $(this).val();				
				if($firstVal == $lastVal){
					//console.log($firstVal,$lastVal)
					$select.val($lastVal).removeClass('hide');
					$input.removeClass('on').attr("readonly",true);
				}
			})
			
			//버튼 클릭
			$btn.click(function(e){
				e.preventDefault();
				var $val = $input.val(),
					$val2 = $select.val();
				if($(this).hasClass('btn_minus')){
					$val--;
					if($val < $min){
						alert('최소수량은 '+$min+'개 입니다.')
						$val = $min;
					}
				}else{
					$val++;
					if($val > $max){
						alert('최대수량은 '+$max+'개 입니다.')
						$val = $max;
					}
				}
				//var last = Math.max($min,Math.min($val,$max))
				
				$input.val($val);
				$select.val($val);
			})
		});
	}	
}

/* 테이블 관련 */
function tableUI(){
	$('table').on('change','.tbl-chk-all',function(){
		var $table = $(this).closest('table'),
			$chk = $table.find('tbody .tbl-chk');

	    if($(this).prop('checked')){
	        $chk.prop('checked', true).closest('tr').addClass('on');
	    }else{
	        $chk.prop('checked', false).closest('tr').removeClass('on')
	    }
	});
	$('table').on('change','.tbl-chk',function(){
        var $table = $(this).closest('table'),
        	$thChk = $table.find('.tbl-chk-all'),
        	$tdChk = $table.find('.tbl-chk'),
        	$length = $tdChk.length,
        	$checked = $tdChk.filter(":checked").length;

        if( $length == $checked){
            $thChk.prop('checked', true);
        }else{
            $thChk.prop('checked', false);
        }

        if($(this).prop('checked')){
           $(this).closest('tr').addClass('on')
        }else{
           $(this).closest('tr').removeClass('on')
        }
    });
    
	//trClick
    $('.tr_click').on('click','tbody tr',function(){
    	var $chk = $(this).find('.tbl-chk');

		$(this).toggleClass('on');
    	if($chk.length > 0){
			$chk.trigger('click');
    	}
    }).on('click','.tbl-chk',function(e) {
		e.stopPropagation();
	});;

    //tbl_row
	$(window).load(function(){
		if($('.tbl_row').length > 0){
			$('.tbl_row tbody tr:odd').addClass('even');
		}
    })
}

/* TOP 버튼 */
function topBtn() {
    var settings = {
            button      : '#toTop',
            text        : '컨텐츠 상단으로 이동',
            min         : 100,
            fadeIn      : 400,
            fadeOut     : 400,
            scrollSpeed : 800,
            easingType  : 'easeInOutExpo'
        }
    $('body').append('<a href="#" id="' + settings.button.substring(1) + '" title="' + settings.text + '">' + settings.text + '</a>');
    $( settings.button ).on('click', function( e ){
        $('html, body').animate({ scrollTop : 0 }, settings.scrollSpeed, settings.easingType );
        e.preventDefault();
    })
    .on('mouseenter', function() {
        $( settings.button ).addClass('hover');
    })
    .on('mouseleave', function() {
        $( settings.button ).removeClass('hover');
    });

    $(window).scroll(function() {
        var position = $(window).scrollTop();       
        if ( position > settings.min ) { $( settings.button ).fadeIn( settings.fadeIn );  }
        else { $( settings.button ).fadeOut( settings.fadeOut );  }
    });
}

/* 알럿 */
function alertTip(tar,txt) {
	var $this = $(tar),
		$delay = 1000,
		$speed = 300;

	if($this.length > 0){
		var $left = $this.offset().left,
			$top = $this.offset().top,
			$height = $this.outerHeight(),
			$tip = $('<div class="alert_tip" style="left:'+$left+'px;top:'+($top+$height)+'px;">'+txt+'</div>')

		$('body').append($tip);
		$tip.fadeIn($speed).delay($delay).fadeOut($speed,function(){		
			$tip.remove();
		})
		$this.addClass('error').focus().delay($delay).queue(function(next){
			$this.removeClass('error');
			next();
		});
	}
}

/* 중앙정렬 */
function vaMid(tar){
	var $this = $(tar),
		$thisH = $this.outerHeight(),
		$parent = $this.parent(),
		$parentH = $parent.height(),
		$top = Math.max(0,($parentH-$thisH)/2);
	//console.log($parentH , $thisH, $top)

	$this.css({'top':$top});
}

/* 탭 */
function tabmenuUi(){
	var $tab = $('.ui-tabmenu'),
		$wrap = $('.tab_wrap');
		
	$(document).on('click','.ui-tabmenu a',function() {
		if(!$(this).parent().hasClass('on')){
			var href = $(this).attr('href');		
			$(href).show().siblings('.tab_cont').hide();
			$(this).parent().addClass('on').siblings().removeClass('on');
			$(this).parents('.tabmenu').removeClass('tab_open')
		}else{
			$(this).parents('.tabmenu').toggleClass('tab_open')
		}
		return false;
    });
	
	
	$(window).load(function(){
		var speed = 500,
			$href = location.href,
			$tabId = $.url($href).param('tabId'),
			$tabIdx = $.url($href).param('tabIdx'),
			$SclId = $.url($href).param('SclId'),
			$id = $('#'+$SclId);
			
		if($tab.length > 0){	
			$tab.each(function(index, element) {
				var $this = $(this),
					$id2 = $this.attr('id');
				if($id2 == $tabId && $tabIdx > 0){
					$this.children('li').eq($tabIdx).find('a').trigger('click');
				}else{
					$this.children('li').eq(0).find('a').trigger('click');
				}
			});
		}

		if($id.length > 0 && $id.is(':visible')){
			var $top = $id.offset().top;
			$(window).scrollTo($top-$contPt,speed);
		}
	

		if($wrap.length > 0){
			$(window).scroll(function(){
				var $scrollTop = $(this).scrollTop();

				$wrap.each(function(index, element){
					var $this = $(this),
						$top2 = $this.offset().top,
						$st = Math.floor($top2 - $contPt);
					//console.log(index,$scrollTop,$contPt,$st)
					if($st <= $scrollTop){
						//console.log(index)
						$this.addClass('fixed');
					}else{
						$this.removeClass('fixed');
					}
				});
			})
		}
	})
	
}

//팝업
function popOpen(tar){
	var $speed = 300,
		$ease = 'easeOutQuart',
		$pop = $(tar).find('.pop_wrap');
	var $wrapH,$popH,$mT
	
	$('body').addClass('hidden');
	$(tar).fadeIn($speed);
	popPositin(tar)
	$(window).resize(function(){
		popPositin(tar)
	})
}

// 공통 안내 팝업
function popAlert(msg){
	var tar = '#popAlert';
	var $speed = 300,
		$ease = 'easeOutQuart',
		$pop = $(tar).find('.pop_wrap');
	var $wrapH,$popH,$mT
	
	if(msg !== undefined){
		$('#alert_msg').html(msg);
	}
	
	$('body').addClass('hidden');
	$(tar).fadeIn($speed);
	$(tar).css('z-index', 600);
	popPositin(tar)
	$(window).resize(function(){
		popPositin(tar)
	})
}

//공통 confirm 팝업
function popConfirm(msg){
	var tar = '#popConfirm';
	var $speed = 300,
		$ease = 'easeOutQuart',
		$pop = $(tar).find('.pop_wrap');
	var $wrapH,$popH,$mT
	
	if(msg !== undefined){
		$('#confirm_msg').html(msg);
	}
	
	$('body').addClass('hidden');
	$(tar).fadeIn($speed);
	$(tar).css('z-index', 600);
	popPositin(tar)
	$(window).resize(function(){
		popPositin(tar)
	})
}
function popPositin(tar,speed){
	//console.log($(tar).attr('id'))
	var $wrapH = $(tar).height(),
		$pop = $(tar).find('.pop_wrap'),
		$popH = $pop.outerHeight(),
		$mT = Math.max(0,($wrapH-$popH)/2);
	
	$pop.stop().animate({'margin-top':$mT,'opacity':1},300);
}
function popupUI(){
	$(document).on('click','.pop_open',function(e) {
		e.preventDefault();
		var pop = $(this).attr('href');
		popOpen(pop);
	});
	$(document).on('click','.pop_close',function(e) {
		e.preventDefault();
		var pop = $(this).closest('.pop_bg');
		popClose(pop);
	});
}
function popClose(tar) {	
	$('body').removeClass('hidden');
	$(tar).fadeOut(300,function(){
		$(tar).find('.pop_wrap').removeAttr('style')
	})
}
function popClose2(tar) {	
	$('body').removeClass('hidden');
	$(tar).fadeOut(0,function(){
		$(tar).find('.pop_wrap').removeAttr('style')
	})
}

/* SNS 퍼가기 */
function snsCtrl(){
    $('.btn_fb').click(function() {
        snsLinker(0,$(this));
        return false;
    });

    $('.btn_twt').click(function() {
        snsLinker(1,$(this));
        return false;
    });

    $('.btn_m2day').click(function() {
        snsLinker(2,$(this));
        return false;
    });
};

function snsLinker(idx,tar){
    var url   = "";
    var title = document.title;
	var _this = tar;
	var link; 

	if(_this.hasClass('list_sns')){
		var linkTxt  = _this.parent().siblings('a').attr('href');
		link  = 'http://www.kidsnkeys.co.kr' + linkTxt;
	}else{
		link  = document.location;
	}

	//console.log(link);

     switch(idx)
     {
          //페이스북
          case 0 : url = "http://www.facebook.com/sharer.php?u="+link+"&t="+encodeURIComponent(title);break;
          //트위터
          case 1 : url = "http://twitter.com/home?status="+encodeURIComponent(title)+" : "+link; break;
          //미투데이
          case 2 : url = "http://me2day.net/posts/new?new_post[body]='" + encodeURIComponent(title)+"' : "+ link +"&new_post[tags]="+encodeURIComponent("sns달기 테스트"); break;
     }

     var retPop = window.open(url,'sns','height=500px, width=750px');
     if(retPop == null){
          alert("팝업 차단을 사용안함으로 설정해주시기 바랍니다.");
     }
     return false;
}

/*메뉴*/
function menuCtrl() {
	$('.menu_list li').hover(function () {
		$(this).addClass('hover');
		$(this).children('div').stop().hide().slideDown();
	},function () {
		$(this).removeClass('hover');
		$(this).children('div').removeAttr('style');
	});

	$('.menu_list li > a').focus(function () {		
		$(this).parent().addClass('hover').siblings().removeClass('hover').children('.list_cont').removeAttr('style');
		$(this).siblings().stop().hide().slideDown();
	});

	$('.menu_list li:last-child .btn_view').blur(function () {
		$(this).parent().parent().removeClass('hover');
		$(this).parent().stop().removeAttr('style');
	});
	
	$('.menu_view_wrap').on('click','.menu_close',function(e){
		e.preventDefault();
		menuClose();
	});
}
//메뉴 상세보기
function menuOpen(){
	$('body').addClass('hidden');		
	$('.menu_view_wrap').fadeIn(500,function(){
		menuPosition();	
	});
}
function menuPosition(){
	var $w = $(window).height(),
		$h = $('.menu_view').outerHeight(),
		$mt = Math.max(0,($w-$h)/2);
	
	if($('.menu_view').is(':visible')){
		$('.menu_view').stop().animate({marginTop:$mt,opacity:1})
	}
}

function menuClose(){
	$('body').removeClass('hidden');
	$('.menu_view_wrap').fadeOut(300,function(){
		$(this).find('.menu_view').removeAttr('style');
	});
}

$(window).resize(function(){
	menuPosition();
})
	


/*기타*/
 function etcCtrl(){
	//로그아웃
	$(document).on('click','.btn_logout',function() {
		$('.logout_box').show('scale');
		return false;
	});
	$(document).on('click','.logout_close',function() {
		$('.logout_box').hide('scale',function(){
			//$('.btn_logout').focus();
		});
		return false;
	});
	
	//딜리버리 안내
	$('.delibery_open').click(function() {
		$('.delibery_box').show(500);
		return false;
	});
	$('.delibery_close').click(function() {
		$('.delibery_box').hide(500,function(){
			//$('.delibery_open').focus();
		});
		return false;
	});
	//QNA 
	$('.qna_motion dt a').click(function() {
		if($(this).parents('.qna_motion').hasClass('tbl_style')){			
			$(this).parents('tr').toggleClass('on').siblings().removeClass('on');
			$(this).parent().next().slideToggle(300).parents('tr').siblings().find('dd').slideUp(300);;
		}else{
			$(this).parent('dt').toggleClass('on').siblings('dt').removeClass('on');
			$(this).parent().next().slideToggle(300).siblings('dd').slideUp(300);
		}		
		return false;
	});
	$('.qna_motion dd .btn_close').click(function() {		
		$('.qna_motion').find('.on').removeClass('on');
		$('.qna_motion dd').slideUp(300);
		return false;
	});

	//인쇄
	$('.btn_print').click(function() {
		window.print();
		return false;
	});
	
	//딜리버리 유의사항
	$('.delibery_box .left').click(function(){
		$(this).closest('.delibery_box').toggleClass('on');
	})
	
	//tooltip
	$(document).tooltip({
		items:".tooltip, .tooltip-img, [data-tooltip-img]",
		track:true,
		content:function() {
			var element = $( this );
			if(element.is( "[data-tooltip-img]" ) ) {
				var img = element.data('tooltip-img'),
					alt = element.data('tooltip-alt');			
				return "<img src='"+ img +"' alt='"+alt+"'>";
	        }        
	        if(element.hasClass("tooltip-img")){
				return element.attr("alt" );
	        }
			if(element.hasClass("tooltip")){
				return element.attr("title");
	        }
		}
	});
 }

//모바일 에이전트 구분
var isMobile = {
        Android: function () {
                 return navigator.userAgent.match(/Android/i) == null ? false : true;
        },
        BlackBerry: function () {
                 return navigator.userAgent.match(/BlackBerry/i) == null ? false : true;
        },
        IOS: function () {
                 return navigator.userAgent.match(/iPhone|iPad|iPod/i) == null ? false : true;
        },
        Opera: function () {
                 return navigator.userAgent.match(/Opera Mini/i) == null ? false : true;
        },
        Windows: function () {
                 return navigator.userAgent.match(/IEMobile/i) == null ? false : true;
        },
        any: function () {
                 return (isMobile.Android() || isMobile.BlackBerry() || isMobile.IOS() || isMobile.Opera() || isMobile.Windows());
        }
};


function loadingShow(){
	var $loading = $('<div id="loading"><div><img src="/resources/images/common/loading-bar.gif" alt="로딩중" /></div></div>'),
		$id = $('#loading');
	
	if($id.length == 0){
		$('body').append($loading);
	}	
}

function loadingHide(){
	var $id = $('#loading');
	$id.remove();
}