package com.example.clone_daum.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.lang.NumberFormatException
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-13 <p/>
 */

class ThrottleTest {
    // 마지막 아이템만 리턴한다.                  throttleLast
    // 일정시간안에 최초 방출 아이템만 리턴한다.      throttleFirst
    // 일정 시간에 있는 모든 아이템을 무시한다.      throttleWithTimeout

    @Test
    fun throttleFirstTest() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        // 100ms 로 발행
        val early = Observable.just(data[0])
            .zipWith(Observable.timer(100L, TimeUnit.MILLISECONDS)) { a, _ -> a }

        // 300ms 후 발행
        val middle = Observable.just(data[1])
            .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS)) { a, _ -> a}

        // last 4 개는 100ms 후 발행
        val late = Observable.just(data[2], data[3], data[4], data[5])
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) { a, _ -> a}
            .doOnNext(::println)

        val src = Observable.concat(early, middle, late)
            .throttleFirst(200L, TimeUnit.MILLISECONDS)

        src.subscribe {
            println("subscribe : $it (${System.currentTimeMillis()})")
        }

        Thread.sleep(1000)
    }

    @Test
    fun windowTest() {
        val data = arrayOf("1", "2", "3", "4", "5", "6")

        // 3개는 100ms
        val early = Observable.fromArray(data)
            .take(3)
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) {
                a, _ -> a
            }

        // 300ms 후 발행
        val middle = Observable.just(data[3])
            .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS)) {
                a, _ -> a
            }

        // 마지막 2개는 100ms 후 발행
        val late = Observable.just(data[4], data[5])
            .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS)) {
                a, _ -> a
            }

        val src = Observable.concat(early, middle, late)
            .window(3)

        // window 는 group by 와 유사하다
        // 특정 조건에 맞는 입력 값들을 그룹화해서 별도의 observable 을 병렬로 만든다.
        // 반면 window 는 throttleFirst 나 sample 처럼 내가 처리할 수 있는 일부의 값들만
        // 받아들일 수 있다.
        // groupBy 와 비슷한 별도의 Observable 분리 기능을 모두 갖춘것이다.

        src.subscribe {
            println("New observable started!")
            it.subscribe {
                println("== ${it.toString()} ==")
            }
        }

        Thread.sleep(1000)
    }

    @Test
    fun concatTest() {
        val data = arrayOf("1", "2", "3", "5")
        val src = Observable.concat(
            Observable.timer(100L, TimeUnit.MILLISECONDS).map{ i -> data[0] },
            Observable.timer(300L, TimeUnit.MILLISECONDS).map{ i -> data[1] },
            Observable.timer(100L, TimeUnit.MILLISECONDS).map{ i -> data[2] },
            Observable.timer(300L, TimeUnit.MILLISECONDS).map{ i -> data[3] }
        ).debounce(200L, TimeUnit.MILLISECONDS)

        src.subscribe(::println)
        Thread.sleep(1000)
    }

    @Test
    fun assertFailureExample() {
        val data = arrayOf("100", "200", "%300")
        val src = Observable.fromArray(*data).map(Integer::parseInt)

//        src.test().assertFailure(NumberFormatException::class.java, 100, 200)
        src.test().assertFailureAndMessage(NumberFormatException::class.java,
            """for input string: "%300"""", 100, 200)
    }

    @Test
    // @DisplayName     // 이거 동작 안함
    // @Disabled        // 이거 동작 안함
    fun intervalWrongWay() {
        val src = Observable.interval(100L, TimeUnit.MILLISECONDS)
            .take(5)
            .map(Long::toInt)

        src.doOnNext(::println)
            .test().assertResult(0, 1, 2, 3, 4)
    }

    @Test
    fun flowableJustTest() {
        Flowable.just("hello world")
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe(::println, Throwable::printStackTrace)

        Flowable.fromCallable {
                Thread.sleep(1000)
                "Done"
            }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe(::println, Throwable::printStackTrace)

        Thread.sleep(2000)
    }

    @Test
    fun backpressureDropTest() {
        Flowable.range(1, 50_000_000)
            .onBackpressureDrop()
            .observeOn(Schedulers.computation())
            .subscribe({
                Thread.sleep(100)
                println(it)
            }, {
                println(it.message)
            })

        Thread.sleep(20_000)
    }
}