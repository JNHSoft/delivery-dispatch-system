/**
 * 2021.08.10
 * Common function javascript
 * */

/**
 * 시간 변환 함수 모음
 * */
{
    // 시간(초)를 hh:mm:ss 형식으로 변경한다. 24시간 이상 시 hh는 3자리로 표기가 될 수 있다. 밀리세컨드는 x
    function secondsToTime(seconds){
        let result = "-";

        if (seconds > 0){
            let hour = parseInt(seconds / 3600);
            let min = parseInt((seconds % 3600) / 60);
            let sec = parseInt(seconds % 60);

            if (hour < 10){
                hour = ('0' + hour).slice(-2);
            }

            result = hour + ':' + ('0' + min).slice(-2) + ':' + ('0' + sec).slice(-2);
        }

        return result;
    }

    // hh:mm:ss 형식의 시간을 초로 변경한다.
    function timeToSeconds(time){
        let result = 0;

        console.log('time => ', time);

        if (time !== undefined){
            let times = time.split(':').reverse();

            if (times.length <= 3){
                for (let i = 0; i < times.length; i++) {
                    result += parseInt(times[i]) * Math.pow(60, i);
                }
            }else{
                result = -1;            // 시간 형식이 올바르지 않습니다.
            }
        }

        return result;
    }

    // 밀리세컨드로 시간 형태로 변경하기
    function millisecondToTime(millisecond){
        if (millisecond) {
            if(millisecond>=0){
                let d = new Date(millisecond);
                return ('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
            }else{
                millisecond = Math.abs(millisecond);
                let d = new Date(millisecond);
                return "-"+('0' + d.getUTCHours()).slice(-2) + ':' + ('0' + d.getUTCMinutes()).slice(-2) + ':' + ('0' + d.getUTCSeconds()).slice(-2);
            }
        } else {
            return "-";
        }
    }
    
    // 날짜 형식의 string으로 받은 경우 날짜로 변환하기 mm-dd hh:mm
    function dateStringToDateTime(strDate){
        if (strDate){
            let d = new Date(strDate);
            return $.datepicker.formatDate('mm-dd ', d) + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2);
        }else{
            return "-";
        }
    }

    // 날짜 형식의 string으로 받은 경우 날짜로 변환하기 yyyy.mm.dd
    function dateStringToDate(strDate){
        if (strDate) {
            let d = new Date(strDate);
            return $.datepicker.formatDate('yy.mm.dd ', d);
        } else {
            return "-";
        }
    }

}

/**
 * 숫자형식 변환 함수 모음
 * */
{
    // 반올림
    function formatInt(sender){
        if (sender == null || isNaN(sender)){
            return 0;
        }

        return parseInt(parseFloat(sender).toFixed(0));
    }
    
    // 특정 자리 수 기준의 반올림 1의자리까지만 허용
    function formatFloat(sender, pointer, comma = false){
        if (sender == null || isNaN(sender)){
            return 0;
        }

        if (comma === true){
            return parseFloat(parseFloat(sender).toFixed(pointer)).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }else{
            return parseFloat(parseFloat(sender).toFixed(pointer));
        }

    }
}