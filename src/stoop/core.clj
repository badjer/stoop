(ns stoop.core
	(:use [clojure.contrib.sql])
	(:require [clojure-yahoo-finance.core :as finance]))

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
