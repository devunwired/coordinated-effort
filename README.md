# Coordinated Effort

This repository contains a collection of example custom [behaviors](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.Behavior.html) for [CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html) part of the [Design Support Library](http://android-developers.blogspot.com/2015/05/android-design-support-library.html).

# Disclaimer

This repository contains sample code intended to demonstrate the capabilities of the CoordinatorLayout API. It is not intended to be used as-is in applications as a library dependency—or a stand-alone production application—and will not be maintained as such. Bug fix contributions are welcome, but issues and feature requests will not be addressed.

# Sample Contents

This example application currently contains the following items:

- `.behaviors`
 - `.FooterBarBehavior` - Simple position follower for an additional view to piggyback onto the default quick-return behavior of `AppBarLayout`.
 - `.QuickHideBehavior` - A faster quick-return behavior that can be applied to any sibling view of the scrolling child. Uses animations instead of scroll position tracking.
 - `.SlidingCardBehavior` - Completely custom behavior to implement a deck of overlapping + sliding card views, each containing a scrolling list (via `RecyclerView`).
- `.widget`
 - `.FooterBarLayout` - Simple widget explaining how to attach a default behavior to a view.
 - `.SlidingCardLayout` - Custom card widget for `SlidingCardBehavior` that provides additional layout data to the behavior.

Each of these items is contained in an example activity + layout to expose how they would be used in practice.

# License

The code supplied here is covered under the MIT Open Source License:

Copyright (c) 2015 Wireless Designs, LLC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.