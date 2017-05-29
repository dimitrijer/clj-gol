(ns clj-gol.core
  (:require [overtone.at-at :refer [mk-pool every]]
            [seesaw.core :as s])
  (:import clj_gol.swing.DrawingPanel)
  (:gen-class))

(defn range-inc [start end] (range start (inc end)))

(defn clamp [x [xmin xmax]] (max (min x xmax) xmin))

(defn make-cells
  "Creates a seq of cells for a fixed size board, using `alive-f` to set initial
  cell state. This function takes cell coordinates and returns a boolean."
  ([rows cols]
   (make-cells rows cols (fn [_ _] (> 0.5 (rand)))))
  ([rows cols alive-f]
   (for [i (range rows)
         j (range cols)]
     {:i i
      :j j
      :alive? (alive-f i j)})))

(defn make-board
  ([rows cols]
   (make-board (make-cells rows cols) rows cols))
  ([cells rows cols]
   ;; Convert seq to vector matrix so we get constant access time.
   (with-meta (vec (map vec (partition cols cells)))
              {:size [rows cols]})))

(defn find-neighbours
  ([board cell]
   (find-neighbours board (:i cell) (:j cell)))
  ([board i j]
   (for [row (range-inc (dec i) (inc i))
         col (range-inc (dec j) (inc j))
         :let [original? (and (= row i) (= col j))
               [rows cols] (:size (meta board))
               safe-row (clamp row [0 (dec rows)])
               safe-col (clamp col [0 (dec cols)])]
         :when (not original?)]
     (get-in board [safe-row safe-col]))))

(defn toggle [cell] (update-in cell [:alive?] not))

(defn should-toggle?
  [board cell]
  (let [neighbours (find-neighbours board cell)
        alive (count (filter :alive? neighbours))
        underpopulated? (< alive 2)
        overpopulated? (> alive 3)
        reproduction? (= alive 3)]
    (if (:alive? cell)
      (or underpopulated?
          overpopulated?)
      reproduction?)))

(defn evolve-cell
  [board cell]
  (if (should-toggle? board cell)
    (toggle cell)
    cell))

(defn evolve-board
  [board]
  (let [[rows cols] (:size (meta board))
        evolved-cells (map (partial evolve-cell board) (flatten board))]
    (make-board evolved-cells rows cols)))

(defn -main
  [& args]
  (let [rows 64
        cols 64
        board (atom (make-board rows cols))
        panel (DrawingPanel.)
        frame (s/frame :title "GOL",
                       :content panel
                       :on-close :exit)
        sched-pool (mk-pool)]
    (s/invoke-later
      (-> frame
          s/show!))
    (every 100 (fn []
                 (.setBoard panel (swap! board evolve-board))
                 (s/pack! frame)) sched-pool)))
