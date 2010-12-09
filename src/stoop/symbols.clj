(ns stoop.symbols
	(:require [clojure.string :as string]))

(defn read-symbol-list [filename]
	(let [contents (string/split-lines (slurp filename))
			lines (rest contents)] ; first line is a header	
		(map #(first (string/split % #"\t")) lines)))
