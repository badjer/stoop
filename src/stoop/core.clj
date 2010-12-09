(ns stoop.core
	(:use [clojure.contrib.sql])
	(:import [java.sql.Date]
			[org.joda.time LocalDate])
	(:require [clojure-yahoo-finance.core :as finance]
				[stoop.symbols :as symbols]
				[clojure.string :as string]))

(def db {:classname "org.postgresql.Driver"
		:subprotocol "postgresql"
		:subname "//localhost:5432/stoop"
		:user "postgres"
		:password "abc123"})

(defn read-db []
	"Reads everything in the db"
	(with-connection db 
		(with-query-results rs ["select * from dpoint"]
			(dorun (map #(println %) rs)))))

(defn- sql_date [s]
	"Converts a JODA date to a sql date for use by clojure.contrib.sql
	Taken from www.paullegato.com/blog/clojure-joda-sql-date-time"
	(java.sql.Date. (.. (LocalDate. s) toDateMidnight toInstant getMillis)))

(defn parse-quote [sym quotestring]
	"Converts a quotestring into a map of data that can be used with the db"
	(let [strs (rest (string/split-lines quotestring))
			parts (fn [line] (string/split #"," line))
			dict (fn [ps] (hash-map 
				:symbol sym
				:date (sql_date (first ps))
				:openp (BigDecimal. (second ps))
				:highp (BigDecimal. (nth ps 2))
				:lowp (BigDecimal. (nth ps 3))
				:closep (BigDecimal. (nth ps 4))
				:volume (BigDecimal. (nth ps 5))
				:adjp (BigDecimal. (nth ps 6))))]
		(map #(dict (parts %)) strs)))

(defn insert-db [dpoints]
	"Inserts a collection of datapoints (dpoint) into the database"
	(with-connection db (apply insert-records "dpoint" dpoints)))

(defn load-db [start end sym]
	"Loads historical stock data for sym from start to end to the database"
	(let [raw (finance/blocking-query start end [sym])
		  quotestring (raw sym)
		  data (parse-quote sym quotestring)]
		(insert-db data)))


; To load namespace at repl:
; (require '(clojure.contrib [string :as string]))

