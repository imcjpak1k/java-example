package org.example.java8.util;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * GenericBuilder
 * Vo객체 생성 및 값입력 하는 반복적인 작업을 "Generic Pattern + lambda"를 이용하여
 * 간편하게 사용할 수 있다.
 *
 * 추가) lombok 라이브러리에 이 기능이 있음.
 *
 * Ex)
 * Employee e = VoBuilder.build(Employee::new)
 *          .with(v -> v.setName("test1"))
 *          .with(v -> v.setSalary(10000))
 *          .with(Employee::setDepartment, "보험코어개발티")
 *          .get();
 *
 * @param <T>
 */
public class VoBuilder<T> {
    private T instance;
    private boolean ifContinue = true;

    private VoBuilder(T t){
        this.instance  = t;
    }

    /**
     * {@link VoBuilder} 생성객체생성 후 반환
     * @param s
     * @return
     * @param <T>
     */
    public static <T> VoBuilder<T> build(Supplier<T> s) {
        return new VoBuilder<>(s.get());
    }

    /**
     * with적용여부
     *
     * @param condition
     * @return
     */
    public VoBuilder<T> If(BooleanSupplier condition) {
        this.ifContinue = condition.getAsBoolean();
        return this;
    }

    /**
     * vo객체에 직접입력
     *
     * 예) with(v -> v.setName("이름")
     *
     * @param setter
     * @return
     */
    public VoBuilder<T> with(Consumer<T> setter) {
        if(ifContinue) {
            setter.accept(instance);
        }

        return this;
    }

    /**
     * vo의 setter호출
     * 예) with(TestVo::setName, "이름")
     *
     * @param setter
     * @param v
     * @return
     * @param <V>
     */
    public <V> VoBuilder<T> with(BiConsumer<T, V> setter, V v) {
        if(ifContinue) {
            setter.accept(instance, v);
        }

        return this;
    }

    /**
     * 인턴스객체반환
     *
     * @return
     */
    public T get() {
        return instance;
    }
}