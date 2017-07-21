package com.bestvike.linq.iterator;

import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.IEnumerator;
import com.bestvike.linq.IEqualityComparer;
import com.bestvike.linq.function.Func1;
import com.bestvike.linq.function.Func2;
import com.bestvike.linq.impl.Lookup;

/**
 * @author 许崇雷
 * @date 2017/7/17
 */
public class GroupJoinIterator<TOuter, TInner, TKey, TResult> extends AbstractIterator<TResult> {
    private IEnumerable<TOuter> outer;
    private IEnumerable<TInner> inner;
    private Func1<TOuter, TKey> outerKeySelector;
    private Func1<TInner, TKey> innerKeySelector;
    private Func2<TOuter, IEnumerable<TInner>, TResult> resultSelector;
    private IEqualityComparer<TKey> comparer;
    private Lookup<TKey, TInner> lookup;
    private IEnumerator<TOuter> outerEnumerator;

    public GroupJoinIterator(IEnumerable<TOuter> outer, IEnumerable<TInner> inner, Func1<TOuter, TKey> outerKeySelector, Func1<TInner, TKey> innerKeySelector, Func2<TOuter, IEnumerable<TInner>, TResult> resultSelector, IEqualityComparer<TKey> comparer) {
        this.outer = outer;
        this.inner = inner;
        this.outerKeySelector = outerKeySelector;
        this.innerKeySelector = innerKeySelector;
        this.resultSelector = resultSelector;
        this.comparer = comparer;
    }

    @Override
    public AbstractIterator<TResult> clone() {
        return new GroupJoinIterator<>(this.outer, this.inner, this.outerKeySelector, this.innerKeySelector, this.resultSelector, this.comparer);
    }

    @Override
    public boolean moveNext() {
        switch (this.state) {
            case 1:
                this.lookup = Lookup.createForJoin(this.inner, this.innerKeySelector, this.comparer);
                this.outerEnumerator = this.outer.enumerator();
                this.state = 2;
            case 2:
                if (this.outerEnumerator.moveNext()) {
                    TOuter item = this.outerEnumerator.current();
                    this.current = this.resultSelector.apply(item, this.lookup.get(this.outerKeySelector.apply(item)));
                    return true;
                }
                this.close();
                return false;
            default:
                return false;
        }
    }

    @Override
    public void close() {
        this.lookup = null;
        if (this.outerEnumerator != null) {
            this.outerEnumerator.close();
            this.outerEnumerator = null;
        }
        super.close();
    }
}
