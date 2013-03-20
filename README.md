# dbkid

Simple debugging facilities.

## Note
### Not-A-Real-Debugger
This is not really a debugger, so it cannot be used to step through code executions.
### Only-To-Capture-Running-Values
This simple tool kit only offers a convenient way to capture values when running code.
### Has-Limits
This is not a perfect tool - it has its own limits. E.g. cannot capture binding Vars. 
### Let-Me-No....
Please let me(xiejianming@gmail.com) know if you have anything (or bug report) to say....

## Usage
### Briefing...
There are two functions(well, they are macros actually) can help to investigate values during execution; and along with their own switches:
- 'dbk' with switch of 'db' (debug)
- '?' with switch of 'bp' (break-point)

We can use them in two scenarios:
- to print values in code by using function 'dbk'
- by using '?', we can stop execution at some point so that we can query the runtime env.

in both cases, you can use

     (db)
     ;; or
     (bp)
     
to turn off them - so that you can shut them up and it's safe to leave any 'dbk'/'?' in your code.

### 1 use dbkid.core
E.g. 

     (use 'dbkid.core)

### 2 use 'dbk' - a convenient printing function
E.g.

     (let [x 123 y (range 10)]
        (dbk x y (map #(* % %) y))
        "Done!")

then we will get:

     Debug: 2013-03-20 00:42:47 in NO_SOURCE_PATH@2: {x => 123, y => (0 1 2 3 4 5 6 7 ...), (map (fn* [p1__1420#] (* p1__1420# p1__1420#)) y) => (0 1 4 9 16 25 36 49 ...)}
     "Done!"
     
#### 2.1 turn off dbk

     (db)
     (let [x 123 y (range 10)]
          (dbk x y (map #(* % %) y))
          "Done!")
     
and now we get:

     DBK is now Disabled!
     nil
     "Done!"
     
#### 2.2 turn on dbk
just 

     (db)
     
it again....

### 3 use '?' - an interactive "break-point"
Try following code:

     (def ^:dynamic xx 111111)
     (binding [xx "this is xx"]
       (let [a 123 b (defn tt[] (println a)) e [1 tt 3 (println 999)]]
          (let [a 789 c "wtf" d (range 11) f {1 2 3 (range 3) 4 (rand)} 
               g (fn [] (println "ggggggg")) h (def xxx 222222)]
            (println a) (println b) (println c)
            (println d) (println e) (println f)
            (println g) (println h)
            (? println xx))))

Note: if above code doesn't surprise you, please turn on '?' and then try them again.

#### 3.1 turn on/off break-point
Use 

     (bp)

to turn on/off break-point
#### 3.2 bp instructions
When you see a prompt, following instructions make bp work its way:
- '?' - print the current line info of break-point 
- 'l' - list locals
- 'll' - list 'global' Vars
- local/var name(e.g. 'a' in above example) - print the value of given local/var
- normal clojure form(e.g. '(bp)' to turn it off in following executions) - eval the given form
- ENTER - get through current break point

## License 

Copyright (c) xjm (xiejianming@gmail.com). All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
