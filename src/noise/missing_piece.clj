(ns noise.missing-piece
  (:use [overtone.live]
        [overtone.synth.stringed]))

(def m (metronome 70))
(def g1 (guitar))

(defn convert-pair [p]
  (let [[i e] p]
    [i (read-string e)]))

(defn read-tab-line
  "Reads a tab line and converts it to a sequence of vector [beats :up/:down] pairs"
  [tab]
  (let [entries (clojure.string/split tab #"\s+")
        with-time (for [[i e] (map-indexed vector entries)]
                    [(* 0.25 i) e])
        notes (filter #(not= "~" (second %)) with-time)]
   (map convert-pair notes) 
        ))


(defn play [guitar notes]
  (let [beat (m)]
    (doseq [[offset fret] notes]
      (apply-at (m (+ beat offset))
        guitar-strum [guitar [-1 fret -1 -1 -1 -1]]))))

; A simple melody line
(play g1 (read-tab-line "3 8 10 12 ~ 10 12 13 12 ~ 17 ~ ~ 15 ~ 13 12 13 12 ~"))

(defn tab-to-timed-chords
  [tab]
  ; Group vertically, discard empty columns, attach timestamps, discard rests
  ; Just hardcode something for now to get the playing working:
  [[0 [0 2 2 -1 -1 -1]]
   [1 [3 5 5 -1 -1 -1]]]
  )

(defn play-tab
  [guitar & lines]
  (let [beat (m)]
    (doseq [[offset chord] (tab-to-timed-chords lines)]
      (apply-at (m (+ beat offset))
                guitar-strum [guitar chord]))))

(play-tab g1 
  "    7           10      12 - - 10 -       14 15 -"
  "  8   8 -    12    12 -                12        "
  "8         10            13 - - 12 - 13           ")
