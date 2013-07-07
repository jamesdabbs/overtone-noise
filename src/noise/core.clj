(ns noise.core 
  (:use 
    [overtone.live]))

; Simple instrument controls
(definst foo [freq 440 phase 0.0] (sin-osc freq phase))

(defn adjust
  [inst attr msg] 
  (let [
    rval (first (:args msg))
    sval (scale-range rval 0 1 50 1000)]
    (ctl inst attr sval)))

; OSC server setup
(defonce server (osc-server 44100 "osc-clj"))

(defonce kick  (sample (freesound-path 171104)))
(defonce snare (sample (freesound-path 26903)))
(defonce c-hat (sample (freesound-path 802)))
(defonce o-hat (sample (freesound-path 813)))

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

  ; /1
  (osc-handle server "/1/fader1" (partial adjust foo :freq))
  (osc-handle server "/1/fader2" (partial adjust foo :phase))
  ; (osc-handle server "/1/toggle1" ())
  ; (osc-handle server "/1/toggle2" ())
  (osc-handle server "/1/push1" (strike kick))
  ; (osc-handle server "/1/push2" (strike ))
  ; (osc-handle server "/1/push3" (strike ))
  ; (osc-handle server "/1/push4" (strike ))
  ; (osc-handle server "/1/push5" (strike ))
  ; (osc-handle server "/1/push6" (strike ))
  (osc-handle server "/1/push7" (strike snare))
  (osc-handle server "/1/push8" (strike o-hat))
  (osc-handle server "/1/push9" (strike c-hat))
  ; (osc-handle server "/1/push10" (strike ))
  ; (osc-handle server "/1/push11" (strike ))
  ; (osc-handle server "/1/push12" (strike ))

  ; /2
  ; /2/multifader/1-16
  ; /2/multitoggle/1-6/1-16

  ; /3
  ; /3/rotary1-6
  ; /3/toggle1-5
  
  ; /4
  ; /4/xy
  ; /4/toggle1-5
)


(defn automat5 []
  (clear-bindings)

  ; /1
  ; /1/rotaryA-D
  ; /1/faderA-D
  ; /toggleA-D_1-2
  ; /encoderM
  ; /multifaderM/1-4
  ; /faderM

  ; /2
  ; /2/multitoggle1-3/1-4/1-2 ... note duplication of 1
  ; duplicated toggles

  ; /3
  ; /3/multipushM/1-2/1-4
  ; /3/xyM_l & /3/xyM_r
  ; duplicated toggles
)

