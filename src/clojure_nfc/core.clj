(ns clojure-nfc.core
  (:import (javax.smartcardio TerminalFactory CommandAPDU))
  (:gen-class))

(def success-status 36864)
(def read-uid-command (new CommandAPDU (byte-array [(unchecked-byte 0xFF) (unchecked-byte 0xCA) 0 0 0])))

(defn readable [bytes]
  (map #(format "%02X" %) bytes))

(defn read-uid [channel]
  (readable (.getData (.transmit channel read-uid-command))))

(defn get-reader []
  (try
    (-> (TerminalFactory/getDefault)
        (.terminals)
        (.list)
        (.get 0))
    (catch Exception e (println e) nil)))

(defn get-card [reader]
  (println "Waiting for card:")
  (. reader waitForCardPresent 0)
  (-> reader
      (.connect "T=0")
      (.getBasicChannel)))

;; TODO: FIX THIS?
;; (. reader waitForCardAbsent 0)

(defn -main
  [& args]
  (println (read-uid (get-card (get-reader)))))
