package com.bestvike.linq.iterator;

import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.IEnumerator;
import com.bestvike.linq.function.Func2;

/**
 * @author 许崇雷
 * @date 2017/7/16
 */
public class SelectManyIterator2<TSource, TResult> extends AbstractIterator<TResult> {
    public IEnumerable<TSource> source;
    private Func2<TSource, Integer, IEnumerable<TResult>> selector;
    private IEnumerator<TSource> enumerator;
    private IEnumerator<TResult> enumerator2;
    private int index;

    public SelectManyIterator2(IEnumerable<TSource> source, Func2<TSource, Integer, IEnumerable<TResult>> selector) {
        this.source = source;
        this.selector = selector;
    }

    @Override
    public AbstractIterator<TResult> clone() {
        return new SelectManyIterator2<>(this.source, this.selector);
    }

    @Override
    public boolean moveNext() {
        do {
            switch (this.state) {
                case 1:
                    this.index = -1;
                    this.enumerator = this.source.enumerator();
                    this.state = 2;
                case 2:
                    if (this.enumerator.moveNext()) {
                        TSource item = this.enumerator.current();
                        this.index = Math.addExact(this.index, 1);
                        this.enumerator2 = this.selector.apply(item, this.index).enumerator();
                        this.state = 3;
                        break;
                    }
                    this.close();
                    return false;
                case 3:
                    if (this.enumerator2.moveNext()) {
                        this.current = this.enumerator2.current();
                        return true;
                    }
                    this.enumerator2.close();
                    this.state = 2;
                    break;
                default:
                    return false;
            }
        } while (true);
    }

    @Override
    public void close() {
        if (this.enumerator != null) {
            this.enumerator.close();
            this.enumerator = null;
        }
        if (this.enumerator2 != null) {
            this.enumerator2.close();
            this.enumerator2 = null;
        }
        super.close();
    }
}
