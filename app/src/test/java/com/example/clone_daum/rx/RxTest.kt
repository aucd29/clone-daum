package com.example.clone_daum.rx

import android.util.Log
import brigitte.toDateString
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-12 <p/>
 */

class RxTest {
    @Test
    fun errorOnReturnTest() {
        val grades = arrayOf("70", "88", "$100", "93", "83")
        Observable.fromArray(*grades)
            .map { data -> Integer.parseInt(data) }
            .onErrorReturn { e ->
                if (e is NumberFormatException) {
                    // TODO $100 값이 문제를 일으킨다.
                    e.printStackTrace()
                }

                // 반환 값이 0보다 작으면 오류를 출력 함
                return@onErrorReturn -1
            }
            .subscribe { data ->
                if (data < 0) {
                    Log.e("", "Wrong data found!!")
                    return@subscribe
                }

                Log.i("", "Grade is $data")
            }
    }

    @Test
    fun errorOnSubscribeTest() {
        val grades = arrayOf("70", "88", "$100", "93", "83")
        Observable.fromArray(*grades)
            .map { data -> Integer.parseInt(data) }
            .subscribe({
                Log.i("", "Grade is $it")
            }, {
                if (it is java.lang.NumberFormatException) {
                    it.printStackTrace()
                }

                Log.e("", "Wrong data found!!")
            })
    }

    @Test
    fun errorResumeNextTest() {
        val salesData = arrayOf("100", "200", "a300")

        // defer : 비 동기로 observable 를 생성하고 하위 스트림에서 사용할 수 있도록 해준다.
        val parseError = Observable.defer {
            println("send email to administrator")  // 오류 발생 시 이벤트 발생

            Observable.just(-1)
        }.subscribeOn(Schedulers.io()) // working io schedulers

        val src = Observable.fromArray(*salesData)
            .map(Integer::parseInt)
            .onErrorResumeNext(parseError)  // 오류 발생 시

        src.subscribe {
            if (it < 0) {
                println("wrong data found!!")
                return@subscribe
            }

            println("sales data : $it")
        }
    }

    @Test
    fun retryUntilTest() {
        val networkState = false
        val url = "https://api.github.com/zen"
        val src = Observable.just(url)
            .map { _ -> "convert data" }
            .subscribeOn(Schedulers.io())
            .retryUntil {
                if (networkState) {
                    println("network available")
                    return@retryUntil true      // 중지
                }

                println("continuous")

                Thread.sleep(1000)
                false                           // 계속 진행
            }
        src.subscribe(::println)
    }

    @Test
    fun retryWhenTest() {
        Observable.create<String> {
            it.onError(RuntimeException("always fails"))
        }.retryWhen { it ->
            return@retryWhen it.zipWith(Observable.range(1, 3))
                { _, i -> i }.flatMap {
                    val dateString = System.currentTimeMillis().toDateString()
                    println("delay retry by $it seconds ($dateString)")
                    Observable.timer(it.toLong(), TimeUnit.SECONDS)
                }
        }.blockingForEach(::println)    // blockingForEach 은 subscribe 가 종료될 때까지 대기하게 된다.
    }

    @Test
    fun flowControlSampleTest() {
        val dataList = arrayOf("1", "7", "2", "3", "6")

        // 시간 측정용
        println(System.currentTimeMillis().toDateString())

        // 앞의 4개는 100ms 간격으로 발행
        val earlySrc = Observable.fromArray(*dataList)
            .take(4)
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("early = $a")
                a
            }

        // 마지막에 데이터는 300ms 후에 발행
        val lastSrc = Observable.just(dataList[4])
            .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("last = $a")
                a
            }

        // sample 실행이 끝나지 않았는데 observable 이 종료 되는 경우에 마지막 값을 발행하려면
        // emitLast 인자를 true 로 넣어주면 된다.
        val showLastValue = true

        // 2개의 Observable 을 결합하고 300ms 로 샘플링
        val src = if (showLastValue) {
            Observable.concat(earlySrc, lastSrc)
                .sample(300L, TimeUnit.MILLISECONDS, true)
        } else {
            Observable.concat(earlySrc, lastSrc)
                .sample(300L, TimeUnit.MILLISECONDS)
        }

        src.subscribe {
            println("                           ==== $it ====")

            // 300ms 간격으로 가장 최근에 들어온 값만 최종적으로 발행 됨
            println(System.currentTimeMillis().toDateString())
        }

        Thread.sleep(1000)

        // 고로 7, 3 만 발행
        // showLastValue 값이 true 면 7, 3, 6 을 발행
    }

    @Test
    fun bufferTest() {
        val dataList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        // 100ms 간격으로 발행
        val early = Observable.fromArray(*dataList).take(3)
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("1)= $a")

                a
            }

        // 300ms 후 발생
        val middle = Observable.just(dataList[3])
            .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("2)= $a")

                a
            }

        // 100ms 후 발행
        val last = Observable.just(dataList[4], dataList[5])
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("3)= $a")

                a
            }

        // 3개씩 발행
        val src = Observable.concat(early, middle, last)
            .observeOn(Schedulers.io())
            .buffer(2, 3)
        src.subscribe(::println)

        Thread.sleep(2000)
    }

    @Test
    fun throttleFirstTest() {
        val dataList = arrayOf("1", "2", "3", "4", "5", "6")

        val early = Observable.just(dataList[0])
            .zipWith(Observable.timer(100L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("1)= $a")

                a
            }

        val middle = Observable.just(dataList[1])
            .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("2)= $a")

                a
            }

        val last = Observable.just(dataList[2], dataList[3], dataList[4], dataList[5])
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) { a, _ ->
                println("3)= $a")

                a
            }.doOnNext(::println)

        val src = Observable.concat(early, middle, last)
            .throttleFirst(200L, TimeUnit.MILLISECONDS)

        src.subscribe(::println)
        Thread.sleep(1000)
    }

    @Test
    fun observableDefer() {
        val ob = Observable.defer<String> {
            Observable.just("hello world")
        }
    }

    @Test
    fun flatMapTest() {
        val balls = arrayOf("1","3","4")
        val src = Observable.fromArray(*balls)
            .flatMap {
                Observable.just("$it◇$it◇")
            }
        src.subscribe(::println)
    }

    @Test
    fun gugudan() {
        val dan = 2
        val src = Observable.just(dan)
            .flatMap { num ->
                Observable.range(1, 9)
                    .map { "$num * $it = ${dan * it}"}
            }
            .subscribe(::println)

    }
}