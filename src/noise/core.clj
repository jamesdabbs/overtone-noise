(ns noise.core 
  (:use 
    [overtone.live]))

; Simple instrument controls
(definst foo [freq 440] (sin-osc freq))

(defn control-foo 
 [val] 
 (let [val (scale-range val 0 1 50 1000)]
      (ctl foo :freq val)))

(definst c-hat [amp 0.8 t 0.04]
  (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
        noise (white-noise)
        sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
        filt (bpf (+ sqr noise) 9000 0.5)]
    (* amp env filt)))

; OSC server setup
; ? How to reload with this and (use 'noise.core :reload)
(def server (osc-server 44100 "osc-clj"))

(def kick  (sample (freesound-path 171104)))
(def snare (sample (freesound-path 26903)))
(def c-hat (sample (freesound-path 802)))
(def o-hat (sample (freesound-path 813)))

; ? better way to express conditional
; ? cutoff note on release
(defn strike 
  [inst]
  (fn [msg] 
    (if (= 1.0 (first (:args msg)))
      (inst))))

(defn clear-bindings []
  (map (partial osc-rm-handler server) [
    "/1/fader1" "/1/fader2" 
    "/1/push1" "/1/push2" "/1/push3" "/1/push4" "/1/push5" 
    "/1/push6" "/1/push7" "/1/push8" "/1/push9"]))

(defn beatmachine []
  (clear-bindings)

  (osc-handle server "/1/fader1" (fn [msg] (control-foo (first (:args msg)))))
  (osc-handle server "/1/fader2" (fn [msg] (control-foo (first (:args msg)))))
  (osc-handle server "/1/push1" (strike kick))
  ; (osc-handle server "/1/push2" (strike ))
  ; (osc-handle server "/1/push3" (strike ))
  ; (osc-handle server "/1/push4" (strike ))
  ; (osc-handle server "/1/push5" (strike ))
  ; (osc-handle server "/1/push6" (strike ))
  (osc-handle server "/1/push7" (strike snare))
  (osc-handle server "/1/push8" (strike o-hat))
  (osc-handle server "/1/push9" (strike c-hat))
)

