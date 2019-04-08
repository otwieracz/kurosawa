(ns org.purefn.kurosawa.config
  "Fetch configuration from the environment.

  Presently, config is stored statefully in an `atom` after initial load."
  (:refer-clojure :exclude [set])
  (:require [org.purefn.kurosawa.config.env :as env]
            [org.purefn.kurosawa.config.file :as file]
            [org.purefn.kurosawa.util :refer [compile-if]]
            [taoensso.timbre :as log]))

(def ^:private config-map
  (atom nil))

(defn set
  "Set the `config-map` atom to configuration sourced from the envinronment,
  probably from:

  - `org.purefn.kurosawa.config.file/fetch`
  - `org.purefn.kurosawa.config.env/fetch`, or
  - `org.purefn.kurosawa.aws.ssm/fetch`"
  [m]
  (reset! config-map m))

;; avoid a hard dependency on `aws.ssm` from this project.
(compile-if
 (do (require 'org.purefn.kurosawa.aws.ssm)
     true)
 (defn- fetch-ssm
   []
   (org.purefn.kurosawa.aws.ssm/fetch
    (or (org.purefn.kurosawa.aws.ssm/prefix-from-env-var)
        "/local/platform")))
 (defn- fetch-ssm
   []
   (log/warn "Tried to load org/purefn/kurosawa/aws/ssm.clj but it was"
             "not found in the classpath!")))

(defn default-config
  "This is our default, precendence based, load config from environment
  mechasnism.  A shallow merge was chosen to make final merged config map easier
  to reason about.  Current precendence is:

  1) Environemt variables
  2) AWS SSM Paramter Store
  3) The filesystem (legacy)

  Ultimately we shouldn't need this, the config stage of application/development
  startup needs to revisited.  But until all of our components' constructors are
  refactored this isn't possible.

  When that day comes we can remove this nasty `compile-if`, and the atom, and move to
  a stateless startup sequence, where each component recieves the entire config map and
  parses out the piece it's interested in."
  []
  (merge (file/fetch "/etc/")
         (fetch-ssm)
         (env/fetch)))

(defn fetch
  ([]
   (if @config-map
     @config-map
     (reset! config-map (default-config))))
  ([name]
   (get (fetch) name)))
