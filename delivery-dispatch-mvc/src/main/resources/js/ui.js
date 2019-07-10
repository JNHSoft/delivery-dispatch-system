/*<![CDATA[*/
$(function(){
	headerUI();
	layoutUI();
	formStyle();
	tableUI();
	popupUI();
	tapMotion();
	//topBtn();
	etcUI();
});

//resizeEnd
$(window).resize(function() {
	if(this.resizeTO) {
	    clearTimeout(this.resizeTO);
	}
	this.resizeTO = setTimeout(function() {
	    $(this).trigger('resizeEnd');
	},300);
});
function layoutUI(){
	if($('#contents').length > 0){
		// $(window).on('load resize',function(){
			var $winH = $(this).height(),
				$con = $('#contents'),								
				$headH = $con.offset().top,
				$footH = $('#footer').outerHeight(),
				$height = $winH-$headH-$footH;
			
			$con.removeAttr('style');
			var $conH = $con.outerHeight();

			if($height > $conH){
				$con.css({'height':$height})
			}
		// })
	}

	if($('.same-height').length > 0){
		sameHeight('.same-height');
	}
}
function sameHeight(tar){	
	$(window).on('load resize',function(){
		var maxHeight = 0;		
		$(tar).removeAttr('style');
		$(tar).each(function(){
		   if ($(this).outerHeight() > maxHeight) { maxHeight = $(this).outerHeight(); }
		});	
		$(tar).outerHeight(maxHeight);
	})
}
function headerUI(){
	var $title = $('#pageTit').text(),
		$gnbOn = $('#gnbOn').text();
	
	if(!$title == ''){
		document.title = $title + ' | PizzaHut Driver Dispatch';
		$('.head_tit').empty().text($title);
	}
		
	gnbActive($title);

	/*timer*/
	var myDate = new Date()
	var year = myDate.getYear()
	var month = myDate.getMonth() +1
	var day = myDate.getDate()
	var dday=new Array(gnb_sunday,gnb_monday,gnb_tuesday,gnb_wednesday,gnb_thursday,gnb_friday,gnb_saturday);
	var weekend = myDate.getDay();
	//var hours = myDate.getHours();
	//var min = myDate.getMinutes();
	year = (year < 1000) ? year + 1900 : year;
    $('.head_timer .date').html("<span><b>" + year + "</b>" + gnb_year + "</span><span><b>" + month + "</b>" + gnb_month + "</span><span><b>" + day +  "</b>" + gnb_day + "</span><span>" + dday[weekend] + "</span>")

	//clock
	var clock = $('.head_timer .clock').FlipClock({
		clockFace: 'TwentyFourHourClock'
	});

	$('.btn_gnb').click(function(event) {
		event.preventDefault();
		$('body').toggleClass('gnb_open');
	});
};

function gnbActive(txt){
	$('#gnb a').each(function() {
		 if ( $(this).text() == txt) {
			$(this).parents('li').addClass('on');
		}
	});
	$('.1depth').on('click',function(e){
		e.preventDefault();
		$(this).next('dl').toggleClass('on');

	})
}

/*폼요소*/
function formStyle(){
	//checkbox, radio
	$('.checkbox input, .radio input').focus(function(){
		$(this).parent().addClass('on');
	}).blur(function(){
		$(this).parent().removeClass('on');
	});

	//datepicker
	if($('.picker').length > 0){
        $.datepicker.setDefaults({
			closeText: '닫기',
			prevText: datepicker_prev_month,
			nextText: datepicker_next_month,
			currentText: '오늘',
			monthNamesShort: ['01','02','03','04','05','06','07','08','09','10','11','12'],
			monthNames: ['01','02','03','04','05','06','07','08','09','10','11','12'],
			dayNamesMin: [datepicker_sunday,datepicker_monday,datepicker_tuesday,datepicker_wednesday,datepicker_thursday,datepicker_friday,datepicker_saturday],
			dateFormat: 'yy-mm-dd',
			showMonthAfterYear: true,
			showOn: 'both',
			buttonText: '기간조회',
			changeMonth: true,
			changeYear: true
		});
	}
	
	if($('.datepicker_month').length > 0){
        $.datepicker.setDefaults({
			closeText: '닫기',
			prevText: datepicker_prev_month,
			nextText: datepicker_next_month,
			currentText: '오늘',
			monthNamesShort: ['01','02','03','04','05','06','07','08','09','10','11','12'],
			monthNames: ['01','02','03','04','05','06','07','08','09','10','11','12'],
			dayNamesMin: [datepicker_sunday,datepicker_monday,datepicker_tuesday,datepicker_wednesday,datepicker_thursday,datepicker_friday,datepicker_saturday],
			dateFormat: 'yy-mm',
			showMonthAfterYear: true,
			showOn: 'both',
			buttonText: '기간조회',
			changeMonth: true,
			changeYear: true
		});
	}
	
	//팝업내에서 스크롤시 달력 숨김
	$('.pop_wrap').scroll(function(){
		$('.picker').datepicker('hide');
		$('.datepicker_month').datepicker('hide');
	})
	

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
			//$('.tbl_row tbody tr:odd').addClass('even');
		}
  })
}


//팝업
function popOpen(tar){
	var $speed = 300,
		$ease = 'easeOutQuart',
		$pop = $(tar).find('.popup');
	var $wrapH,$popH,$mT
	var $visible = $('.pop_wrap:visible').size();
	
	$('body').addClass('hidden');
	if($visible > 0){
		$(tar).css('z-index','+='+$visible)
	}	
	$(tar).fadeIn($speed);
	popPositin(tar,300)
	$(window).on('resizeEnd',function(){
		popPositin(tar,300)
	})
}
function popPositin(tar,speed){
	//console.log($(tar).attr('id'))
	var $wrapH = $(tar).height(),
		$pop = $(tar).find('.popup'),
		$popH = $pop.outerHeight(),
		$mT = Math.max(0,($wrapH-$popH)/2);
	if(speed > 100){
		$pop.stop().animate({'margin-top':$mT},speed);
	}else{
		$pop.css({'margin-top':$mT});
	}
}
function popupUI(){
	$('.pop_open').click(function(e) {
		e.preventDefault();
		var pop = $(this).attr('href');
		popOpen(pop);
	});
	$('.pop_close').click(function(e) {
		e.preventDefault();
		var pop = $(this).closest('.pop_wrap');
		popClose(pop);
	});
}
function popClose(tar) {	
	$('body').removeClass('hidden');
	$(tar).fadeOut(300,function(){
		$(tar).removeAttr('style');
		$(tar).find('.popup').removeAttr('style');
	})
}

function alertTip(tar,txt) {
	var $this = $(tar),
		$delay = 5000,
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

/* 탭 */
function tapMotion(){
	var $tab = $('.tab_motion'),
		$wrap = $('.tab_wrap');
		
	$tab.on('click','a',function() {
		if(!$(this).parent().hasClass('on')){
			var href = $(this).attr('href');		
			$(href).addClass('on').siblings('.tab_cont').removeClass('on');
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
			$tabIdx = $.url($href).param('tabIdx');
			
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
	})
	
	$('.rdo_tabmenu .radio input').change(function(){
		var $val = $(this).val()
		$('#'+$val).addClass('on').siblings('.rdo_cont').removeClass('on');
	})

	if($('.rdo_tabmenu').length > 0){
		$('.rdo_cont').eq(0).addClass('on');
	}
}


/* TOP 버튼 */
function topBtn(){
	var settings ={
			button		:'#btnTop',
			text		:'컨텐츠 상단으로 이동',
			min			:100,
			fadeIn		:400,
			fadeOut		:400,
			scrollSpeed :800,
			easingType  :'easeInOutExpo'
		}

	$('body').append('<a href="#" id="' + settings.button.substring(1) + '" title="' + settings.text + '"><i class="fa fa-arrow-up" aria-hidden="true"></i><span class="blind">' + settings.text + '</span></a>');
	$(settings.button).on('click', function(e){
		$('html, body').animate({scrollTop :0}, settings.scrollSpeed, settings.easingType);
		e.preventDefault();
	})
	.on('mouseenter', function(){
		$(settings.button).addClass('hover');
	})
	.on('mouseleave', function(){
		$(settings.button).removeClass('hover');
	});

	$(window).scroll(function(){
		var position = $(window).scrollTop();
		if(position > settings.min){$(settings.button).fadeIn(settings.fadeIn);}
		else{$(settings.button).fadeOut(settings.fadeOut);}
	});
}

function etcUI(){
	//faq_list
	$('.faq_list').on('click','> dt > a',function() {
		$(this).parent('dt').toggleClass('on').siblings('dt').removeClass('on');
		$(this).parent().next().slideToggle(300).siblings('dd').slideUp(300);
		return false;
	});	
	$('.faq_list').on('click','>li> p>a',function(){
		$(this).parent().next('div').slideToggle(300).parent().toggleClass('on').siblings('li').removeClass('on').children('div').slideUp(300);
		return false;
	});	

	// 숫자 업시키는 클래스 .countUp
	if($('.countUp').length > 0){
		$(window).load(function(){
			$('.countUp').counterUp({
				delay:10,
				time:1000
			});
		})
	}

	//tooltip
	$(document).tooltip({
    items:".tooltip, .ui-jqgrid td, .tooltip-img, [data-tooltip-img]",
	  track:true,
	  content:function() {
	    var element = $(this);
	    if(element.is("[data-tooltip-img]")){
				var img = element.data('tooltip-img'),
					alt = element.data('tooltip-alt');			
				return "<img src='"+ img +"' alt='"+alt+"'>";
	    }        
	    if(element.hasClass("tooltip-img")){
				return element.attr("alt");
	    }
			if(element.hasClass("tooltip")){
				return element.attr("title");
	    }
	    if(element.is(".ui-jqgrid td")){
				return element.attr("title");
	    }
	  }
	});

	//btn_scroll
	$('.btn_scroll').click(function(e){
		e.preventDefault();
		var $speed = 500,
			$href = $(this).attr('href'),
			$id = $($href);

		if($id.length > 0 && $id.is(':visible')){
			var $top = $id.offset().top;
			$(window).scrollTo($top-$contPt,$speed);
		}
	})
}

function loadingShow(){
	var $loading = $('<div id="loading"><div><p>G</p><p>N</p><p>I</p><p>D</p><p>A</p><p>O</p><p>L</p></div></div>'),
		$id = $('#loading');
	
	if($id.length == 0){
		$('body').append($loading);
	}
	
}

function loadingHide(){
	var $id = $('#loading');
	$id.remove();
}


function chatUI() {
	$('.chat_list').on('click','a',function(e){
		e.preventDefault();
		if(!$(this).closest('li').hasClass('on')){
			$(this).closest('li').addClass("on").siblings().removeClass('on');
		}
	});
	$('.chat_list li').each(function(){
		var $writer = $(this).find('.chat_writer'),
			$text = $writer.text();
		$writer.parent().before('<div><span class="ic">'+$text.charAt(0)+'</span></div>');
	})
}

function resizeJqGrid(gridId){
	$(window).on('resize.jqGrid', function () {
    gridParentWidth = $(gridId).closest('.ui-jqgrid').parent().width()-2;
    $(gridId).jqGrid('setGridWidth',gridParentWidth);
	});	
}
/*]]>*/