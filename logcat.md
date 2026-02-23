2026-02-23 10:22:47.947  3287-4060  OplusAppQu...Controller system_server                        E  notifyStartActivityFromRecents task:Task{55c552 #23448 type=standard A=10436:com.runesandrocks.client} mStartFromRecents:true mStartFromSlide:false callingUid:10200
2026-02-23 10:22:47.961  3287-3367  QosSceneRe...tartScene] system_server                        E  notifyActivityStart get launchedFromUid failed through AppManager! launchedFromPackage: com.android.shell, pkg: com.runesandrocks.client
2026-02-23 10:22:48.043 16618-16618 nativeloader            com.runesandrocks.client             D  Load libframework-connectivity-tiramisu-jni.so using APEX ns com_android_tethering for caller /apex/com.android.tethering/javalib/framework-connectivity-t.jar: ok
---------------------------- PROCESS STARTED (16618) for package com.runesandrocks.client ----------------------------
2026-02-23 10:22:48.047 16618-16618 OplusActiv...eadExtImpl com.runesandrocks.client             D  java.lang.NoSuchMethodException: dalvik.system.VMRuntime.SupressionGC [int, int]
2026-02-23 10:22:48.047 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "persist.vendor.sys.activitylog"
2026-02-23 10:22:48.047 16618-16618 OneTrace                com.runesandrocks.client             I  Mark active for pid=16618? true
2026-02-23 10:22:48.048 16618-16618 cutils-dev              com.runesandrocks.client             D  otrace_set_tracing_enabled? true tag:0
2026-02-23 10:22:48.048 16618-16624 cutils-dev              com.runesandrocks.client             D  properties changed in otrace_seq_number_changed!
2026-02-23 10:22:48.078  3287-3385  OplusThermalStats       system_server                        E  Error getting package info: com.runesandrocks.client
2026-02-23 10:22:48.079 16618-16636 OplusActivityManager    com.runesandrocks.client             D  get AMS extension: android.os.BinderProxy@37d94a3
2026-02-23 10:22:48.079 16618-16636 OplusAppHeapManager     com.runesandrocks.client             D  java.lang.NoSuchMethodException: dalvik.system.VMRuntime.updateProcessValue [int, int, int]
2026-02-23 10:22:48.080 16618-16636 SurfaceSyncGroup        com.runesandrocks.client             D  preCreateSurfaceSyncGroupTimerThread
2026-02-23 10:22:48.082 16618-16636 ColorModeChangeItem     com.runesandrocks.client             D  preExecute mColorMode=0
2026-02-23 10:22:48.101 16618-16618 re-initialized>         com.runesandrocks.client             W  type=1400 audit(0.0:4990): avc:  granted  { execute } for  path="/data/data/com.runesandrocks.client/code_cache/startup_agents/f943a550-agent.so" dev="dm-79" ino=1586060 scontext=u:r:untrusted_app:s0:c180,c257,c512,c768 tcontext=u:object_r:app_data_file:s0:c180,c257,c512,c768 tclass=file app=com.runesandrocks.client
2026-02-23 10:22:48.105 16618-16618 nativeloader            com.runesandrocks.client             D  Load /data/user/0/com.runesandrocks.client/code_cache/startup_agents/f943a550-agent.so using system ns (caller=<unknown>): ok
2026-02-23 10:22:48.110 16618-16618 androcks.client         com.runesandrocks.client             W  hiddenapi: DexFile /data/data/com.runesandrocks.client/code_cache/.studio/instruments-76719913.jar is in boot class path but is not in a known location
2026-02-23 10:22:48.135  3287-3358  OplusAppQu...Controller system_server                        E  scheduleHideStartingSurface addAfterPrepareSurface for:ActivityRecord{108014623 u0 com.runesandrocks.client/.android.AndroidLauncher
2026-02-23 10:22:48.291 16618-16618 androcks.client         com.runesandrocks.client             W  Redefining intrinsic method java.lang.Thread java.lang.Thread.currentThread(). This may cause the unexpected use of the original definition of java.lang.Thread java.lang.Thread.currentThread()in methods that have already been compiled.
2026-02-23 10:22:48.291 16618-16618 androcks.client         com.runesandrocks.client             W  Redefining intrinsic method boolean java.lang.Thread.interrupted(). This may cause the unexpected use of the original definition of boolean java.lang.Thread.interrupted()in methods that have already been compiled.
2026-02-23 10:22:48.295 16618-16618 CompactWindowAppManager com.runesandrocks.client             D  initCompactApplicationInfo CompactMode: , NormalMode: 
2026-02-23 10:22:48.297 16618-16618 Configuration           com.runesandrocks.client             V  Updating configuration, locales updated from [] to [en_US]
2026-02-23 10:22:48.298 16618-16618 OplusScrollToTopManager com.runesandrocks.client             D  setIsInWhiteList false
2026-02-23 10:22:48.298 16618-16618 OplusViewDebugManager   com.runesandrocks.client             D  OplusViewDebugManager Constructor android.view.debug.OplusViewDebugManager@fe7d9cc [mHasViewDebugProperty true] [mHasMsgDebugProperty false] [DEBUG false] [EXTRAINFOENABLE false] [LIGHT_OS false]
2026-02-23 10:22:48.304 16618-16618 CompatChangeReporter    com.runesandrocks.client             D  Compat change id reported: 202956589; UID 10436; state: ENABLED
2026-02-23 10:22:48.305 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "ro.odm.prev.product.name"
2026-02-23 10:22:48.377 16618-16618 nativeloader            com.runesandrocks.client             D  Configuring clns-9 for other apk /data/app/~~qTgTPq1Xb4csXZKRX3_Cew==/com.runesandrocks.client-yVQg3HRp1exkmmgITUsRuw==/base.apk. target_sdk_version=35, uses_libraries=, library_path=/data/app/~~qTgTPq1Xb4csXZKRX3_Cew==/com.runesandrocks.client-yVQg3HRp1exkmmgITUsRuw==/lib/arm64:/data/app/~~qTgTPq1Xb4csXZKRX3_Cew==/com.runesandrocks.client-yVQg3HRp1exkmmgITUsRuw==/base.apk!/lib/arm64-v8a, permitted_path=/data:/mnt/expand:/data/user/0/com.runesandrocks.client
2026-02-23 10:22:48.380 16618-16618 androcks.client         com.runesandrocks.client             I  AssetManager2(0xb400007a385e5728) locale list changing from [] to [en-US]
2026-02-23 10:22:48.381 16618-16618 androcks.client         com.runesandrocks.client             I  AssetManager2(0xb4000079e2a58328) locale list changing from [] to [en-US]
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V  Currently set values for:
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V    angle_gl_driver_selection_pkgs=[""]
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V    angle_gl_driver_selection_values=[""]
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V  com.runesandrocks.client is not listed in per-application setting
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V  ANGLE allowlist from config: 
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V  com.runesandrocks.client is not listed in ANGLE allowlist or settings, returning default
2026-02-23 10:22:48.389 16618-16618 GraphicsEnvironment     com.runesandrocks.client             V  Updatable production driver is not supported on the device.
2026-02-23 10:22:48.392 16618-16618 ResourcesManagerExtImpl com.runesandrocks.client             D  applyConfigurationToAppResourcesLocked app.getDisplayId() return callback.displayId:-1
2026-02-23 10:22:48.397 16618-16618 OplusGraphicsEvent      com.runesandrocks.client             D  OplusGraphicsEventListener create now!
2026-02-23 10:22:48.398 16618-16618 OplusGraphicsEvent      com.runesandrocks.client             D  addEventListener success!
2026-02-23 10:22:48.399 16618-16618 [UAH_CLIENT]            com.runesandrocks.client             E  uahInit enter
2026-02-23 10:22:48.401 16618-16618 androcks.client         com.runesandrocks.client             W  AIBinder_linkToDeath is being called with a non-null cookie and no onUnlink callback set. This might not be intended. AIBinder_DeathRecipient_setOnUnlinked should be called first.
2026-02-23 10:22:48.401 16618-16618 [UAH_CLIENT]            com.runesandrocks.client             I  UahGetFeatureStatus enter
2026-02-23 10:22:48.406 16618-16618 HWUI                    com.runesandrocks.client             I  SupportApps size 0
2026-02-23 10:22:48.407 16618-16618 ExtensionsLoader        com.runesandrocks.client             D  createInstance(64bit) : createExtendedFactory
2026-02-23 10:22:48.407 16618-16618 ExtensionsLoader        com.runesandrocks.client             D  Opened libSchedAssistExtImpl.so
2026-02-23 10:22:48.407 16618-16618 SchedAssist             com.runesandrocks.client             E  open sharedFd failed with error=Permission denied
2026-02-23 10:22:48.407 16618-16618 SchedAssist             com.runesandrocks.client             E  open sharedFd failed with error=Permission denied
2026-02-23 10:22:48.407 16618-16618 SchedAssist             com.runesandrocks.client             E  open sharedFd failed with error=Permission denied
2026-02-23 10:22:48.408 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  createInstance(64bit) : createExtendedFactory
2026-02-23 10:22:48.409 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  Opened libSchedAssistExtImpl.so
2026-02-23 10:22:48.409 16618-16688 DisplayManager          com.runesandrocks.client             I  Choreographer implicitly registered for the refresh rate.
2026-02-23 10:22:48.411 16618-16618 androcks.client         com.runesandrocks.client             I  AssetManager2(0xb4000079e2a58928) locale list changing from [] to [en-US]
2026-02-23 10:22:48.412 16618-16618 ResourcesManagerExtImpl com.runesandrocks.client             D  applyConfigurationToAppResourcesLocked app.getDisplayId() return callback.displayId:0
2026-02-23 10:22:48.417 16618-16618 ashmem                  com.runesandrocks.client             E  Pinning is deprecated since Android Q. Please use trim or other methods.
2026-02-23 10:22:48.420 16618-16688 AdrenoGLES-0            com.runesandrocks.client             I  QUALCOMM build                   : bad34dcf83, I1064ae0a1f
                                                                                                    Build Date                       : 11/28/25
                                                                                                    OpenGL ES Shader Compiler Version: E031.41.03.64
                                                                                                    Local Branch                     : 
                                                                                                    Remote Branch                    : refs/tags/AU_LINUX_ANDROID_LA.VENDOR.13.2.0.11.00.00.856.062
                                                                                                    Remote Branch                    : NONE
                                                                                                    Reconstruct Branch               : NOTHING
2026-02-23 10:22:48.420 16618-16688 AdrenoGLES-0            com.runesandrocks.client             I  Build Config                     : S P 14.1.4 AArch64
2026-02-23 10:22:48.420 16618-16688 AdrenoGLES-0            com.runesandrocks.client             I  Driver Path                      : /vendor/lib64/egl/libGLESv2_adreno.so
2026-02-23 10:22:48.420 16618-16688 AdrenoGLES-0            com.runesandrocks.client             I  Driver Version                   : 0676.76.1
2026-02-23 10:22:48.421 16618-16618 OplusPredi...Controller com.runesandrocks.client             E   NoSuchMethodException 
2026-02-23 10:22:48.421 16618-16618 OplusPredi...Controller com.runesandrocks.client             E   NoSuchMethodException 
2026-02-23 10:22:48.421 16618-16618 OplusPredi...Controller com.runesandrocks.client             D  initPredictiveBackConfig for com.runesandrocks.client.android.AndroidLauncher@e4fbeef initPredictiveBackConfig mShouldInterceptKeyEvent false
2026-02-23 10:22:48.421 16618-16618 WindowOnBackDispatcher  com.runesandrocks.client             D   predictive settings is disabled for com.runesandrocks.client
2026-02-23 10:22:48.427 16618-16688 AdrenoGLES-0            com.runesandrocks.client             I  PFP: 0x01740181, ME: 0x00000000
2026-02-23 10:22:48.427 16618-16618 nativeloader            com.runesandrocks.client             D  Load /data/app/~~qTgTPq1Xb4csXZKRX3_Cew==/com.runesandrocks.client-yVQg3HRp1exkmmgITUsRuw==/base.apk!/lib/arm64-v8a/libgdx.so using class loader ns clns-9 (caller=/data/app/~~qTgTPq1Xb4csXZKRX3_Cew==/com.runesandrocks.client-yVQg3HRp1exkmmgITUsRuw==/base.apk): ok
2026-02-23 10:22:48.451 16618-16688 AdrenoUtils             com.runesandrocks.client             I  <ReadGpuID:407>: Reading chip ID through GSL
2026-02-23 10:22:48.455 16618-16618 oplus.andr...actoryImpl com.runesandrocks.client             I  Unknow feature:IOplusTextViewRTLUtilForUG
2026-02-23 10:22:48.458 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  createInstance(64bit) : createExtendedFactory
2026-02-23 10:22:48.459 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  Opened libeglextimpl.so
2026-02-23 10:22:48.460 16618-16688 PreCache                com.runesandrocks.client             D  [IPreCacheRus]loadRUSConfigs#102 failed to transact: -22 prc:com.runesandrocks.client
2026-02-23 10:22:48.465 16618-16618 Vibrator                com.runesandrocks.client             D  SystemVibrator Created
2026-02-23 10:22:48.485 16618-16618 CompatChangeReporter    com.runesandrocks.client             D  Compat change id reported: 279646685; UID 10436; state: ENABLED
2026-02-23 10:22:48.492 16618-16618 CompatChangeReporter    com.runesandrocks.client             D  Compat change id reported: 309578419; UID 10436; state: ENABLED
2026-02-23 10:22:48.493 16618-16618 DesktopModeFlags        com.runesandrocks.client             D  Toggle override initialized to: OVERRIDE_UNSET
2026-02-23 10:22:48.510 16618-16618 ActivityThread          com.runesandrocks.client             D  ComponentInfo{com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher} checkFinished=false 2
2026-02-23 10:22:48.510 16618-16618 ResourcesManagerExtImpl com.runesandrocks.client             D  applyConfigurationToAppResourcesLocked app.getDisplayId() return callback.displayId:0
2026-02-23 10:22:48.510 16618-16618 AndroidInput            com.runesandrocks.client             I  sensor listener setup
2026-02-23 10:22:48.513 16618-16618 AutofillCl...Controller com.runesandrocks.client             V  onActivityPostResumed()
2026-02-23 10:22:48.513 16618-16618 AutofillCl...Controller com.runesandrocks.client             V  onActivityPostResumed(): Relayout fix enabled
2026-02-23 10:22:48.513 16618-16618 AutofillCl...Controller com.runesandrocks.client             V  forResume(): Not attempting refill.
2026-02-23 10:22:48.514 16618-16618 CompatChangeReporter    com.runesandrocks.client             D  Compat change id reported: 352594277; UID 10436; state: ENABLED
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  init sDebug to false, init sDebugIme to false, init sAlwaysOn to false
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  updateDebugToClass InputMethodManager.DEBUG = false
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  updateDebugToClass ImeFocusController.DEBUG = false
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  updateDebugToClass OnBackInvokedDispatcher.DEBUG = false
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  updateDebugToClass InsetsController.DEBUG = false
2026-02-23 10:22:48.514 16618-16618 OplusInputMethodUtil    com.runesandrocks.client             D  updateDebugToClass BaseInputConnection.DEBUG = false
2026-02-23 10:22:48.534 16618-16618 CompatChangeReporter    com.runesandrocks.client             D  Compat change id reported: 349153669; UID 10436; state: ENABLED
2026-02-23 10:22:48.538 16618-16618 WindowManager           com.runesandrocks.client             D  Add to mViews: com.android.internal.policy.DecorView{a8416c4 I.E...... R.....ID 0,0-0,0 alpha=1.0 viewInfo = }[AndroidLauncher],pkg= com.runesandrocks.client
2026-02-23 10:22:48.540 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  createInstance(64bit) : createExtendedFactory
2026-02-23 10:22:48.546 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  Opened libhwuiextimpl.so
2026-02-23 10:22:48.546 16618-16688 skia                    com.runesandrocks.client             D  setRusSupported enable 1
2026-02-23 10:22:48.551 16618-16618 InsetsController        com.runesandrocks.client             D  Setting requestedVisibleTypes to -16 (was -9)
2026-02-23 10:22:48.551 16618-16618 OplusWindowManager      com.runesandrocks.client             D  get WMS extension: android.os.BinderProxy@9c354e2
2026-02-23 10:22:48.561 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  onDisplayChanged -1 for VRI android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.586 16618-16618 ResourcesManagerExtImpl com.runesandrocks.client             D  applyConfigurationToAppResourcesLocked app.getDisplayId() return callback.displayId:0
2026-02-23 10:22:48.589 16618-16618 OplusBracketLog         com.runesandrocks.client             E  [OplusViewMirrorManager] updateHostViewRootIfNeeded, not support android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.590 16618-16618 SurfaceView             com.runesandrocks.client             I  214293806 onAttachedToWindow
2026-02-23 10:22:48.591 16618-16618 WindowOnBackDispatcher  com.runesandrocks.client             D   predictive settings is disabled for com.runesandrocks.client
2026-02-23 10:22:48.591 16618-16618 WindowOnBackDispatcher  com.runesandrocks.client             D   predictive settings is disabled for com.runesandrocks.client
2026-02-23 10:22:48.592 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  wrapConfigInfoIntoFlags rotation=1, smallestScreenWidthDp=0, residentWS=false, scenario=0, bounds=Rect(0, 0 - 2376, 1080), relayoutAsync=false, flags=0, newFlags=1245708296, title=com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher
2026-02-23 10:22:48.614 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             D  relayoutWindow result, sizeChanged:true, surfaceControlChanged:true, transformHintChanged:true, mSurfaceSize:Point(2376, 1080), mLastSurfaceSize:Point(0, 0), mWidth:-1, mHeight:-1, requestedWidth:2376, requestedHeight:1080, transformHint:4, installOrientation:0, displayRotation:1, isSurfaceValid:true, attr.flag:25232640, tmpFrames:ClientWindowFrames{frame=[0,0][2376,1080] display=[0,0][2376,1080] parentFrame=[0,0][0,0]}, relayoutAsync:false, mSyncSeqId:0
2026-02-23 10:22:48.614 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             W  updateBlastSurfaceIfNeeded, surfaceSize:Point(2376, 1080), lastSurfaceSize:Point(0, 0), format:-3, blastBufferQueue:null
2026-02-23 10:22:48.615 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "vendor.display.enable_optimal_refresh_rate"
2026-02-23 10:22:48.615 16618-16618 BufferQueueConsumer     com.runesandrocks.client             D  [](id:40ea00000000,api:0,p:-1,c:16618) connect: controlledByApp=false
2026-02-23 10:22:48.615 16618-16618 BufferQueueConsumer     com.runesandrocks.client             D  [VRI[AndroidLauncher]#0(BLAST Consumer)0](id:40ea00000000,api:0,p:-1,c:16618) setMaxAcquiredBufferCount: 2
2026-02-23 10:22:48.615 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "vendor.display.enable_optimal_refresh_rate"
2026-02-23 10:22:48.617 16618-16618 ResourcesManagerExtImpl com.runesandrocks.client             D  applyConfigurationToAppResourcesLocked app.getDisplayId() return callback.displayId:0
2026-02-23 10:22:48.621 16618-16688 HWUI                    com.runesandrocks.client             D  createSurface new attribs array
2026-02-23 10:22:48.623 16618-16618 OplusDisplayModeManager com.runesandrocks.client             E  has been initialized.
2026-02-23 10:22:48.623 16618-16688 libEGL                  com.runesandrocks.client             D  this is from skia gles
2026-02-23 10:22:48.623 16618-16688 BufferQueueProducer     com.runesandrocks.client             D  [VRI[AndroidLauncher]#0(BLAST Consumer)0](id:40ea00000000,api:1,p:16618,c:16618) connect: api=1 producerControlledByApp=true
2026-02-23 10:22:48.628 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806, SurfaceView[com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher], layerid: 4864 createBlastSurfaceControls
2026-02-23 10:22:48.630 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "vendor.display.enable_optimal_refresh_rate"
2026-02-23 10:22:48.630 16618-16618 BufferQueueConsumer     com.runesandrocks.client             D  [](id:40ea00000001,api:0,p:-1,c:16618) connect: controlledByApp=false
2026-02-23 10:22:48.630 16618-16618 BufferQueueConsumer     com.runesandrocks.client             D  [cc5dd2e SurfaceView[com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher]#1(BLAST Consumer)1](id:40ea00000001,api:0,p:-1,c:16618) setMaxAcquiredBufferCount: 2
2026-02-23 10:22:48.631 16618-16618 libc                    com.runesandrocks.client             W  Access denied finding property "vendor.display.enable_optimal_refresh_rate"
2026-02-23 10:22:48.631 16618-16618 SurfaceView             com.runesandrocks.client             I  214293806 visibleChanged -- surfaceCreated 
2026-02-23 10:22:48.639 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806 handleSyncNoBuffer
2026-02-23 10:22:48.640 16618-16693 GL2JNIView              com.runesandrocks.client             W  creating OpenGL ES 2.0 context
2026-02-23 10:22:48.640 16618-16693 GL2JNIView              com.runesandrocks.client             W  Returning a GLES 2 context
2026-02-23 10:22:48.641 16618-16693 Surface                 com.runesandrocks.client             D  Surface::dispatchSetGameApi
2026-02-23 10:22:48.641 16618-16618 SurfaceControl          com.runesandrocks.client             I   setExtendedRangeBrightness sc=Surface(name=com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher)/@0x9c69ee1,currentBufferRatio=1.0,desiredRatio=1.0
2026-02-23 10:22:48.642 16618-16693 Surface                 com.runesandrocks.client             D  dispatchSetGameApi pid[16618] tid[16693] GameApi[0]
2026-02-23 10:22:48.642 16618-16693 Surface                 com.runesandrocks.client             D  dispatchSetGameApi res:0
2026-02-23 10:22:48.642 16618-16693 libEGL                  com.runesandrocks.client             D  set game api gles successfully
2026-02-23 10:22:48.642 16618-16693 BufferQueueProducer     com.runesandrocks.client             D  [cc5dd2e SurfaceView[com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher]#1(BLAST Consumer)1](id:40ea00000001,api:1,p:16618,c:16618) connect: api=1 producerControlledByApp=true
2026-02-23 10:22:48.649 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806 dispatchDraw mDrawFinished false isAboveParent false (mPrivateFlags & PFLAG_SKIP_DRAW) 128
2026-02-23 10:22:48.651 16618-16693 AndroidGraphics         com.runesandrocks.client             I  OGL renderer: Adreno (TM) 740
2026-02-23 10:22:48.651 16618-16693 AndroidGraphics         com.runesandrocks.client             I  OGL vendor: Qualcomm
2026-02-23 10:22:48.651 16618-16693 AndroidGraphics         com.runesandrocks.client             I  OGL version: OpenGL ES 3.2 V@0676.76.1 (GIT@bad34dcf83, I1064ae0a1f, 1764326394) (Date:11/28/25)
2026-02-23 10:22:48.652 16618-16693 AndroidGraphics         com.runesandrocks.client             I  OGL extensions: GL_OES_EGL_image GL_OES_EGL_image_external GL_OES_EGL_sync GL_OES_vertex_half_float GL_OES_framebuffer_object GL_OES_rgb8_rgba8 GL_OES_compressed_ETC1_RGB8_texture GL_AMD_compressed_ATC_texture GL_KHR_texture_compression_astc_sliced_3d GL_KHR_texture_compression_astc_ldr GL_KHR_texture_compression_astc_hdr GL_OES_texture_compression_astc GL_EXT_texture_compression_s3tc GL_EXT_texture_compression_s3tc_srgb GL_EXT_texture_compression_rgtc GL_EXT_texture_compression_bptc GL_OES_texture_npot GL_EXT_texture_filter_anisotropic GL_EXT_texture_format_BGRA8888 GL_EXT_read_format_bgra GL_OES_texture_3D GL_EXT_color_buffer_float GL_EXT_color_buffer_half_float GL_EXT_float_blend GL_QCOM_alpha_test GL_OES_depth24 GL_OES_packed_depth_stencil GL_OES_depth_texture GL_OES_depth_texture_cube_map GL_EXT_sRGB GL_OES_texture_float GL_OES_texture_float_linear GL_OES_texture_half_float GL_OES_texture_half_float_linear GL_EXT_texture_type_2_10_10_10_REV GL_EXT_texture_sRGB_decode GL_EXT_texture_compression_astc_decode_mode GL_EXT_texture_mirror_clamp_to_edge GL_EXT_texture_format_sRGB_override GL_OES_element_index_uint GL_EXT_copy_image GL_EXT_geometry_shader GL_EXT_tessellation_shader GL_OES_texture_stencil8 GL_EXT_shader_io_blocks GL_OES_shader_image_atomic GL_OES_sample_variables GL_EXT_texture_border_clamp GL_EXT_EGL_image_external_wrap_modes GL_EXT_multisampled_render_to_texture GL_EXT_multisampled_render_to_texture2 GL_OES_shader_multisample_interpolation GL_EXT_texture_cube_map_array GL_EXT_draw_buffers_indexed GL_EXT_gpu_shader5 GL_EXT_robustness GL_EXT_texture_buffer GL_EXT_shader_framebuffer_fetch GL_ARM_shader_framebuffer_fetch_depth_stencil GL_OES_texture_storage_multisample_2d_array GL_OES_sample_shading GL_OES_get_program_binary GL_EXT_debug_label GL_KHR_blend_equation_advanced GL_KHR_blend_equation_advanced_coherent GL_QCOM_tiled_rendering GL_ANDROID_extension_pack_es31a GL_EXT_primitive_bounding_box GL_OES_standard_derivatives GL_OES_vertex_array_object GL_EXT_disjoint_timer_query GL_KHR_debug GL_EXT_YUV_target GL_EXT_sRGB_write_control GL_EXT_texture_norm16 GL_EXT_discard_framebuffer GL_OES_surfaceless_context GL_OVR_multiview GL_OVR_multiview2 GL_EXT_texture_sRGB_R8 GL_KHR_no_error GL_EXT_debug_marker GL_OES_EGL_image_external_essl3 GL_OVR_multiview_multisampled_render_to_texture GL_EXT_buffer_storage GL_EXT_external_buffer GL_EXT_blit_framebuffer_params GL_EXT_clip_cull_distance GL_EXT_protected_textures GL_EXT_shader_non_constant_global_initializers GL_QCOM_texture_foveated GL_QCOM_texture_foveated2 GL_QCOM_texture_foveated_subsampled_layout GL_QCOM_shader_framebuffer_fetch_noncoherent GL_QCOM_shader_framebuffer_fetch_rate GL_EXT_memory_object GL_EXT_memory_object_fd GL_EXT_EGL_image_array GL_NV_shader_noperspective_interpolation GL_KHR_robust_buffer_access_behavior GL_EXT_EGL_image_storage GL_EXT_blend_func_extended GL_EXT_clip_control GL_OES_texture_view GL_EXT_fragment_invocation_density GL_QCOM_motion_estimation GL_QCOM_validate_shader_binary GL_QCOM_YUV_texture_gather GL_QCOM_shading_rate GL_QCOM_frame_extrapolation GL_IMG_texture_filter_cubic GL_QCOM_render_shared_exponent GL_EXT_polygon_offset_clamp GL_EXT_texture_sRGB_RG8 GL_EXT_depth_clamp GL_EXT_fragment_shading_rate GL_EXT_fragment_shading_rate_primitive GL_EXT_fragment_shading_rate_attachment 
2026-02-23 10:22:48.653 16618-16693 AndroidGraphics         com.runesandrocks.client             I  framebuffer: (8, 8, 8, 8)
2026-02-23 10:22:48.653 16618-16693 AndroidGraphics         com.runesandrocks.client             I  depthbuffer: (16)
2026-02-23 10:22:48.653 16618-16693 AndroidGraphics         com.runesandrocks.client             I  stencilbuffer: (0)
2026-02-23 10:22:48.653 16618-16693 AndroidGraphics         com.runesandrocks.client             I  samples: (0)
2026-02-23 10:22:48.653 16618-16693 AndroidGraphics         com.runesandrocks.client             I  coverage sampling: (false)
2026-02-23 10:22:48.656 16618-16693 AndroidGraphics         com.runesandrocks.client             I  Managed meshes/app: { }
2026-02-23 10:22:48.656 16618-16693 AndroidGraphics         com.runesandrocks.client             I  Managed textures/app: { }
2026-02-23 10:22:48.656 16618-16693 AndroidGraphics         com.runesandrocks.client             I  Managed cubemap/app: { }
2026-02-23 10:22:48.656 16618-16693 AndroidGraphics         com.runesandrocks.client             I  Managed shaders/app: { }
2026-02-23 10:22:48.656 16618-16693 AndroidGraphics         com.runesandrocks.client             I  Managed buffers/app: { }
2026-02-23 10:22:48.665 16618-16688 BLASTBufferQueue        com.runesandrocks.client             D  [VRI[AndroidLauncher]#0](f:0,a:1) acquireNextBufferLocked size=2376x1080 mFrameNumber=1 applyTransaction=true mTimestamp=25945394032497(auto) mPendingTransactions.size=0 graphicBufferId=71373766524928 transform=7
2026-02-23 10:22:48.665 16618-16688 VRI[AndroidLauncher]    com.runesandrocks.client             D  Received frameCommittedCallback lastAttemptedDrawFrameNum=1 didProduceBuffer=true syncBuffer=false
2026-02-23 10:22:48.665 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  createInstance(64bit) : createExtendedFactory
2026-02-23 10:22:48.666 16618-16688 ExtensionsLoader        com.runesandrocks.client             D  Opened libSchedAssistExtImpl.so
2026-02-23 10:22:48.667 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  onDisplayChanged 97 for VRI android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.667 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  onDisplayChanged 98 for VRI android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.667 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  onDisplayChanged 98 for VRI android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.667 16618-16618 ViewRootImplExtImpl     com.runesandrocks.client             D  onDisplayChanged 99 for VRI android.view.ViewRootImpl@2cf23a9
2026-02-23 10:22:48.670 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             W  handleResized, msg:, frameChanged:false, configChanged:false, displayChanged:false, attachedFrameChanged:false, compatScaleChanged:false, pendingDragResizing=false
2026-02-23 10:22:48.674 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             W  handleResized, msg:, frameChanged:false, configChanged:false, displayChanged:false, attachedFrameChanged:false, compatScaleChanged:false, pendingDragResizing=false
2026-02-23 10:22:48.674 16618-16618 DynamicFra...igManager] com.runesandrocks.client             I  initFrameRateConfig: 
                                                                                                    	levelsFor120hz: [120, 90, 72, 60]
                                                                                                    	thresholdsFor120hz: [1080, 300, 200]
                                                                                                    	levelsFor90hz: [90]
                                                                                                    	thresholdsFor90hz: []
2026-02-23 10:22:48.674 16618-16618 DynamicFra...igManager] com.runesandrocks.client             I  FRTCConfigManager: FRTC_CAPABILITY = 360, package name = com.runesandrocks.client, WINDOW_ANIMATION_SPEED_RATE = 10, SCROLL_ANIMATION_SPEED_RATE = 20, PACKAGE_ENABLE = false
2026-02-23 10:22:48.674 16618-16618 DynamicFra...ontroller] com.runesandrocks.client             I  init info: mPackageName = com.runesandrocks.client, mIsEnabled = false
2026-02-23 10:22:48.686 16618-16688 VRI[AndroidLauncher]    com.runesandrocks.client             D  Received frameCommittedCallback lastAttemptedDrawFrameNum=2 didProduceBuffer=false syncBuffer=false
2026-02-23 10:22:48.686 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             D  draw finished. seqId=0
2026-02-23 10:22:48.687 16618-16618 DynamicFra...igManager] com.runesandrocks.client             I  initFrameRateConfig: 
                                                                                                    	levelsFor120hz: [120, 90, 72, 60]
                                                                                                    	thresholdsFor120hz: [1080, 300, 200]
                                                                                                    	levelsFor90hz: [90]
                                                                                                    	thresholdsFor90hz: []
2026-02-23 10:22:48.687 16618-16618 DynamicFra...igManager] com.runesandrocks.client             I  FRTCConfigManager: FRTC_CAPABILITY = 360, package name = com.runesandrocks.client, WINDOW_ANIMATION_SPEED_RATE = 10, SCROLL_ANIMATION_SPEED_RATE = 20, PACKAGE_ENABLE = false
2026-02-23 10:22:48.687 16618-16618 DynamicFra...ontroller] com.runesandrocks.client             I  init info: mPackageName = com.runesandrocks.client, mIsEnabled = false
2026-02-23 10:22:48.719 16618-16618 VRI[AndroidLauncher]    com.runesandrocks.client             D  onFocusEvent true
2026-02-23 10:22:48.728 16618-16618 InsetsController        com.runesandrocks.client             D  hide(ime(), fromIme=false)
2026-02-23 10:22:48.728 16618-16618 ImeTracker              com.runesandrocks.client             I  com.runesandrocks.client:c55a4f35: onCancelled at PHASE_CLIENT_ALREADY_HIDDEN
2026-02-23 10:22:48.778 16618-16693 VRI[AndroidLauncher]    com.runesandrocks.client             D  draw finished. seqId=0
2026-02-23 10:22:48.778 16618-16693 SurfaceView             com.runesandrocks.client             D  214293806 finishedDrawing
2026-02-23 10:22:48.778 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806 performDrawFinished mAttachedToWindow true
2026-02-23 10:22:48.787 16618-16693 BLASTBufferQueue        com.runesandrocks.client             D  [cc5dd2e SurfaceView[com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher]#1](f:0,a:1) acquireNextBufferLocked size=2376x1080 mFrameNumber=1 applyTransaction=true mTimestamp=25945515519997(auto) mPendingTransactions.size=0 graphicBufferId=71373766524929 transform=7
2026-02-23 10:22:48.797 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806 dispatchDraw mDrawFinished true isAboveParent false (mPrivateFlags & PFLAG_SKIP_DRAW) 128
2026-02-23 10:22:48.797 16618-16618 SurfaceView             com.runesandrocks.client             D  214293806 clearSurfaceViewPort mCornerRadius 0.0
2026-02-23 10:22:49.032 16618-16718 InteractionJankMonitor  com.runesandrocks.client             W  Initializing without READ_DEVICE_CONFIG permission. enabled=false, interval=1, missedFrameThreshold=3, frameTimeThreshold=64, package=com.runesandrocks.client
2026-02-23 10:22:49.181 16618-16624 androcks.client         com.runesandrocks.client             I  Compiler allocated 7207KB to compile void android.view.ViewRootImpl.performTraversals()
2026-02-23 10:22:49.721 16618-16698 OplusScrollToTopManager com.runesandrocks.client             D  com.runesandrocks.client/com.runesandrocks.client.android.AndroidLauncher,This com.android.internal.policy.DecorView{a8416c4 V.E...... R....... 0,0-2376,1080 alpha=1.0 viewInfo = }[AndroidLauncher] change focus to true
2026-02-23 10:22:53.705 16618-16800 ProfileInstaller        com.runesandrocks.client             D  Installing profile for com.runesandrocks.client
