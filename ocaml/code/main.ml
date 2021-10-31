
(* Problem 1 *)
(* Write a function remove_all : int list -> int -> int list such that
remove_all list m returns a list in the same order as the input list,
but with all the numbers equal to m removed. You may not use any
library functions. *)

let rec remove_all list m = 
  match list with
  [] -> []
  | hd :: tl -> if m = hd then remove_all tl m else hd::remove_all tl m;;

(* remove_all [2; 4; 3; 7; 2; 8; 2] 2;; *)
(* - : int list = [4; 3; 7; 8] *)

(* Problem 2 *)
(* Write a function all_from_to : int -> int -> (int -> bool) -> int
such that all_from_to m n p tells the number of integers greater than
or equal to m and also less than or equal to n which satisfy a given
predicate p : int -> bool.  You may not use any library functions. *)

let rec all_from_to m n p = 
  if m > n then 0 else
  if(p m)= true then (all_from_to (m+1) n p)+1 else all_from_to (m+1) n p;;

(* all_from_to (-5) 7 ((<) 0);; *)
(* - : int = 7 *)

(* all_from_to 3 7 (fun x -> x mod 2 = 0);; *)
(* - : int = 2 *)

(* Problem 3 *)
(* Write a function separate : ('a -> bool) -> 'a list -> int * int
such that separate p l returns a pair of integers, the first indicates
the number of elements of l for which p returns true, and the second
indicates the number of elements for which p returns false. You may
not use any library functions. *)

let rec separate p l = 
  match l with
  [] -> (0,0)
  | hd :: tl -> if (p hd) = true then match separate p tl with (t,f) -> (t+1,f) else match separate p tl with (t,f) -> (t,f+1);;

(* We check which ones match, and on the returns, we increment one to the given entry *)

(* separate (fun x -> x mod 2 = 0) [-3; 5; 2; -6];; *)
(* - : int * int = (2, 2) *)

(* Problem 4 *)
(* Write a function all_even : int list -> bool that returns whether
every element in the input list is even. You may use mod for testing
whether an integer is even. You may not use any other library functions. *)

let rec all_even list = 
  match list with
  []->true
  | hd::tl -> if (hd mod 2) = 0 then all_even tl else false;;

(* As soon as one entry fails, the whole thing fails *)

(* all_even [4; 2; 12; 5; 6];; *)
(* - : bool = false *)

(* Problem 5 *)
(* Write a function sum_square : int -> int -> int such that
sum_square m n calculates the sum of the squares of the elements
strictly greater than m and strictly less than n if there are any, and
0 otherwise. You may not use any library functions. *)

let rec sum_square m n = 
  if ((m+2) > n) then 0 else
    ((m+1)*(m+1))+sum_square (m+1) n;;

(* We do m+1 because it is non inclusive *)


(* sum_square 3 9;; *)
(* - : int = 190 *)

(* Problem 6 *)
(* Write a function concat : string -> string list -> string such that
concat s l creates a string consisting of the strings in the list l
concatenated together, with a single space inserted between
consecutive elements. Also all strings equal to s should be
excluded. You may not use any library functions. *)

let rec concat s list =
  let rec cat conS l =
    match l with [] -> conS
      | (h :: t) -> if(h = s) then(cat conS t) else(cat (conS ^ " " ^ h) t)
  in match list with [] -> ""
      | (hd :: tl) -> if(hd = s) then (concat s tl) else (hd ^ cat "" tl);;

(* We keep checking in order the words until we find the first word that doesn't 
match s, then we go through the rest, concatenating when we dont find a match *)

(* concat "hi" ["How"; "are"; "hi"; "you?"];; *)
(* - : string = "How are you?" *)

(* For problems 7 through 9, you will be supplying arguments to the
higher-order functions List.fold_right and List.fold_left. You should
not need to use explicit recursion for any of 7 through 10. *)

(* Problem 7 *)
(* Write a value remove_all_base and function remove_all_rec : int ->
int -> int list -> int list such that (fun list -> List.fold_right
(remove_all_rec m) list remove_all_base) computes the same results as
remove_all of Problem 1. There should be no use of recursion or
library functions in defining remove_all_rec. *)

let remove_all_base = [];;
let remove_all_rec m n r = if m = n then r else n::r;;

(* (fun list -> List.fold_right (remove_all_rec 2) list remove_all_base) [2; 4; 3; 7; 2; 8; 2];; *)
(* - : int list = [4; 3; 7; 8] *)

(* Problem 8 *)
(* Write a value separate_base and function separate_rec : ('a ->
bool) -> 'a -> int * int -> int * int such that (fun p -> fun list ->
List.fold_right (separate_rec p) list separate_base) computes the same
results as separate of Problem 3. There should be no use of recursion
or library functions in defining separate_rec. *)

let separate_base = (0,0);;
let separate_rec p x (tl, fl) = if (p x) then (tl+1,fl) else (tl,fl+1);;

(* (fun p -> fun list -> List.fold_right (separate_rec p) list separate_base) (fun x -> x mod 2 = 0) [-3; 5; 2; -6];; *)
(* - : int * int = (2, 2) *)

(* Problem 9 *)
(* Write a value all_even_base and function all_even_rec : bool -> int
-> bool such that List.fold_left all_even_rec all_even_base computes
the same results as all_even of Problem 4. You may use mod for testing
whether an integer is even. There should be no use of recursion or
other library functions in defining all_even_rec. *)

let all_even_base = true;;
let all_even_rec r x = if (r && (x mod 2 = 0)) then true else false;;

(* List.fold_left all_even_rec all_even_base [4; 2; 12; 5; 6];; *)
(* - : bool = false *)

(* Problem 10 *)
(* Write a function concat2 : string -> string list -> string that
computes the same results as concat of Problem 6. The definition of
concat2 may use List.fold_left : ('a -> 'b -> 'a) -> 'a -> 'b list ->
'a but no direct use of recursion, and no other library functions. *)

let concat2 s list =
  match list with [] -> ""
    | (hd :: tl) ->
    List.fold_left
    (fun accumulator -> fun nextVal -> if(nextVal = s) then(accumulator ^ "") else(
    if(accumulator = "")then(accumulator^ nextVal)else(accumulator ^ " " ^ nextVal)
    )) (if(hd=s)then("")else(hd)) tl;;

    (* We are moving from left to right so if we have ["How"; "are"; "hi"; "you?"] then we start with
      "How" then it accumulator becaomes "How are" and nextVal is "hi" then hi is detected so we dont
    concatenate and then accumulator is still "How are" but now nextVal is "you?" so then accumulator
    concatenates with the nextVal to get "How are you?". This is all because we are folding left

     *)

(* concat2 "hi" ["How"; "are"; "hi"; "you?"];; *)
(* - : string = "How are you?" *)
