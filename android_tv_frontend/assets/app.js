(function() {
  'use strict';

  // =============================================================================
  // PUBLIC_INTERFACE - ANDROID TV OPTIMIZATION MODULE
  // =============================================================================

  /**
   * Scales the 1920x1080 artboard to fit within current viewport while maintaining 
   * aspect ratio and centering. Optimized for Android TV displays.
   */
  function scaleArtboard() {
    var artboard = document.getElementById('artboard');
    var viewport = document.getElementById('viewport');
    if (!artboard || !viewport) {
      console.warn('Artboard or viewport not found for scaling');
      return;
    }

    var vw = viewport.clientWidth;
    var vh = viewport.clientHeight;
    var baseW = 1920;
    var baseH = 1080;

    if (vw <= 0 || vh <= 0) {
      console.warn('Invalid viewport dimensions:', vw, vh);
      return;
    }

    var scale = Math.min(vw / baseW, vh / baseH);

    // Snap scale to 1/1000 increments to reduce subpixel blurring on TV
    var snapped = Math.round(scale * 1000) / 1000;
    
    // Ensure minimum scale for readability
    snapped = Math.max(snapped, 0.5);

    // Use CSS custom property for better integration with responsive design
    document.documentElement.style.setProperty('--scale-factor', snapped);
    artboard.style.transform = 'scale(' + snapped + ')';

    // Center using flexbox parent (better method)
    viewport.style.alignItems = 'center';
    viewport.style.justifyContent = 'center';
    
    // Remove manual positioning for better responsive behavior
    artboard.style.marginLeft = '';
    artboard.style.marginTop = '';
    
    console.debug('Artboard scaled to:', snapped);
  }

  // =============================================================================
  // TV REMOTE NAVIGATION SYSTEM
  // =============================================================================

  var TVNavigation = {
    currentFocusIndex: 0,
    focusableElements: [],
    gridLayout: null,

    /**
     * PUBLIC_INTERFACE
     * Initializes the TV navigation system with D-pad support
     */
    init: function() {
      this.scanFocusableElements();
      this.setupEventListeners();
      this.createGridLayout();
      this.focusFirst();
    },

    /**
     * Scans the DOM for focusable elements and creates navigation array
     */
    scanFocusableElements: function() {
      var selectors = [
        'button[tabindex="0"]',
        '.action-icon[tabindex="0"]',
        '.panel-button-box',
        '[role="button"][tabindex="0"]',
        '[data-focus]'
      ];
      
      this.focusableElements = Array.from(document.querySelectorAll(selectors.join(', ')))
        .filter(function(el) {
          return el.offsetParent !== null && !el.disabled && !el.hidden;
        });

      // Sort elements by visual position (top to bottom, left to right)
      this.focusableElements.sort(function(a, b) {
        var rectA = a.getBoundingClientRect();
        var rectB = b.getBoundingClientRect();
        
        if (Math.abs(rectA.top - rectB.top) < 50) {
          return rectA.left - rectB.left;
        }
        return rectA.top - rectB.top;
      });
      
      console.debug('Found focusable elements:', this.focusableElements.length);
    },

    /**
     * Creates a 2D grid layout for more intuitive navigation
     */
    createGridLayout: function() {
      var elements = this.focusableElements;
      var grid = [];
      var currentRow = [];
      var lastTop = null;

      elements.forEach(function(el, index) {
        var rect = el.getBoundingClientRect();
        
        if (lastTop !== null && Math.abs(rect.top - lastTop) > 50) {
          if (currentRow.length > 0) {
            grid.push(currentRow);
            currentRow = [];
          }
        }
        
        currentRow.push({ element: el, index: index });
        lastTop = rect.top;
      });

      if (currentRow.length > 0) {
        grid.push(currentRow);
      }

      this.gridLayout = grid;
    },

    /**
     * Sets up keyboard event listeners for TV remote navigation
     */
    setupEventListeners: function() {
      var self = this;
      
      document.addEventListener('keydown', function(e) {
        // Prevent default navigation for specific keys
        if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Enter', ' '].includes(e.key)) {
          e.preventDefault();
        }

        switch(e.key) {
          case 'ArrowLeft':
            self.navigateLeft();
            break;
          case 'ArrowRight':
            self.navigateRight();
            break;
          case 'ArrowUp':
            self.navigateUp();
            break;
          case 'ArrowDown':
            self.navigateDown();
            break;
          case 'Enter':
          case ' ':
            self.activateCurrentElement();
            break;
          case 'Escape':
          case 'Backspace':
            self.handleBack();
            break;
        }
      });

      // Handle focus events for visual feedback
      this.focusableElements.forEach(function(el, index) {
        el.addEventListener('focus', function() {
          self.currentFocusIndex = index;
          self.announceElement(el);
        });
      });
    },

    /**
     * Navigation methods for D-pad directions
     */
    navigateLeft: function() {
      if (this.gridLayout) {
        this.navigateInGrid(-1, 0);
      } else {
        this.navigatePrevious();
      }
    },

    navigateRight: function() {
      if (this.gridLayout) {
        this.navigateInGrid(1, 0);
      } else {
        this.navigateNext();
      }
    },

    navigateUp: function() {
      if (this.gridLayout) {
        this.navigateInGrid(0, -1);
      } else {
        this.navigatePrevious();
      }
    },

    navigateDown: function() {
      if (this.gridLayout) {
        this.navigateInGrid(0, 1);
      } else {
        this.navigateNext();
      }
    },

    /**
     * Smart grid navigation that respects visual layout
     */
    navigateInGrid: function(deltaX, deltaY) {
      var current = this.getCurrentGridPosition();
      if (!current) return;

      var newRow = Math.max(0, Math.min(this.gridLayout.length - 1, current.row + deltaY));
      var targetRow = this.gridLayout[newRow];
      
      var newCol;
      if (deltaX !== 0) {
        newCol = Math.max(0, Math.min(targetRow.length - 1, current.col + deltaX));
      } else {
        // When moving vertically, try to maintain horizontal position
        newCol = Math.min(current.col, targetRow.length - 1);
      }

      var targetElement = targetRow[newCol];
      if (targetElement) {
        this.focusElement(targetElement.index);
      }
    },

    /**
     * Gets current element's position in grid
     */
    getCurrentGridPosition: function() {
      for (var row = 0; row < this.gridLayout.length; row++) {
        for (var col = 0; col < this.gridLayout[row].length; col++) {
          if (this.gridLayout[row][col].index === this.currentFocusIndex) {
            return { row: row, col: col };
          }
        }
      }
      return null;
    },

    /**
     * Linear navigation fallback
     */
    navigateNext: function() {
      var next = (this.currentFocusIndex + 1) % this.focusableElements.length;
      this.focusElement(next);
    },

    navigatePrevious: function() {
      var prev = this.currentFocusIndex - 1;
      if (prev < 0) prev = this.focusableElements.length - 1;
      this.focusElement(prev);
    },

    /**
     * Focus management
     */
    focusElement: function(index) {
      if (index >= 0 && index < this.focusableElements.length) {
        this.currentFocusIndex = index;
        var element = this.focusableElements[index];
        element.focus();
        this.highlightElement(element);
      }
    },

    focusFirst: function() {
      if (this.focusableElements.length > 0) {
        this.focusElement(0);
      }
    },

    /**
     * Visual highlight for focused elements
     */
    highlightElement: function(element) {
      // Remove previous highlights and data-focus attributes
      var highlighted = document.querySelectorAll('.tv-focused, [data-focus="true"]');
      highlighted.forEach(function(el) {
        el.classList.remove('tv-focused');
        if (el.hasAttribute('data-focus')) {
          el.setAttribute('data-focus', 'false');
        }
      });

      // Add highlight to current element
      element.classList.add('tv-focused');
      if (element.hasAttribute('data-focus')) {
        element.setAttribute('data-focus', 'true');
      }
      
      // Ensure element is visible
      this.ensureElementVisible(element);
    },

    /**
     * Ensures focused element is visible in viewport
     */
    ensureElementVisible: function(element) {
      var rect = element.getBoundingClientRect();
      var artboard = document.getElementById('artboard');
      var artboardRect = artboard.getBoundingClientRect();

      // Check if element is outside visible area and scroll if needed
      if (rect.bottom > window.innerHeight || rect.top < 0 ||
          rect.right > window.innerWidth || rect.left < 0) {
        element.scrollIntoView({ 
          behavior: 'smooth', 
          block: 'center', 
          inline: 'center' 
        });
      }
    },

    /**
     * Activates the currently focused element
     */
    activateCurrentElement: function() {
      var element = this.focusableElements[this.currentFocusIndex];
      if (element) {
        // Add visual feedback
        element.classList.add('tv-activated');
        setTimeout(function() {
          element.classList.remove('tv-activated');
        }, 150);

        // Trigger click event
        element.click();
        this.announceActivation(element);
      }
    },

    /**
     * Handle back button press
     */
    handleBack: function() {
      this.announceMessage('Navegación hacia atrás');
      // Could implement actual back navigation here
      console.log('Back navigation triggered');
    },

    /**
     * Accessibility announcements
     */
    announceElement: function(element) {
      var label = element.getAttribute('aria-label') || 
                  element.textContent.trim() || 
                  'Elemento interactivo';
      this.announceMessage('Enfocado: ' + label);
    },

    announceActivation: function(element) {
      var label = element.getAttribute('aria-label') || 
                  element.textContent.trim() || 
                  'Elemento';
      this.announceMessage('Activado: ' + label);
    },

    announceMessage: function(message) {
      var announcer = document.getElementById('announcements');
      if (announcer) {
        announcer.textContent = message;
        // Clear after announcement
        setTimeout(function() {
          announcer.textContent = '';
        }, 1000);
      }
    }
  };

  // =============================================================================
  // PROGRAM ACTIONS IMPLEMENTATION
  // =============================================================================

  var ProgramActions = {
    /**
     * PUBLIC_INTERFACE
     * Initializes program-specific actions and button handlers
     */
    init: function() {
      this.setupActionHandlers();
      this.setupPanelButtons();
    },

    /**
     * Sets up event handlers for action buttons
     */
    setupActionHandlers: function() {
      var self = this;

      // Replay button
      var replayBtn = document.getElementById('layer-I4077-14475-456-11843');
      if (replayBtn) {
        replayBtn.addEventListener('click', function() {
          self.handleReplay();
        });
      }

      // Additional action button
      var actionBtn = document.getElementById('layer-I4077-14475-456-11845');
      if (actionBtn) {
        actionBtn.addEventListener('click', function() {
          self.handleAdditionalAction();
        });
      }
    },

    /**
     * Sets up panel button handlers
     */
    setupPanelButtons: function() {
      var self = this;
      var panelButtons = document.querySelectorAll('.panel-button-box');
      
      panelButtons.forEach(function(button, index) {
        button.addEventListener('click', function() {
          self.handlePanelAction(index, button);
        });

        // Add hover effects for better UX
        button.addEventListener('mouseenter', function() {
          button.style.transform = 'translateY(-2px) scale(1.02)';
        });

        button.addEventListener('mouseleave', function() {
          button.style.transform = '';
        });
      });
    },

    /**
     * Action handlers for different buttons
     */
    handleReplay: function() {
      console.log('Replay action triggered');
      this.showActionFeedback('Reproduciendo desde el inicio...');
    },

    handleAdditionalAction: function() {
      console.log('Additional action triggered');
      this.showActionFeedback('Acción ejecutada');
    },

    handlePanelAction: function(index, button) {
      var actions = [
        'Recordatorio programado',
        'Reiniciando programa...',
        'Grabación iniciada',
        'Agregado a favoritos',
        'Mostrando más información',
        'Configuración de audio/subtítulos'
      ];

      var message = actions[index] || 'Acción ejecutada';
      console.log('Panel action ' + index + ':', message);
      this.showActionFeedback(message);
    },

    /**
     * Shows visual feedback for actions
     */
    showActionFeedback: function(message) {
      // Create or update feedback element
      var feedback = document.getElementById('action-feedback');
      if (!feedback) {
        feedback = document.createElement('div');
        feedback.id = 'action-feedback';
        feedback.style.cssText = `
          position: fixed;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          background: rgba(46, 52, 64, 0.95);
          color: #eceff4;
          padding: 20px 40px;
          border-radius: 16px;
          font-family: 'Reddit Sans', sans-serif;
          font-size: 24px;
          font-weight: 600;
          text-align: center;
          z-index: 9999;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
          border: 1px solid rgba(236, 239, 244, 0.2);
          opacity: 0;
          transition: opacity 0.3s ease;
        `;
        document.body.appendChild(feedback);
      }

      feedback.textContent = message;
      feedback.style.opacity = '1';

      // Announce for screen readers
      var announcer = document.getElementById('announcements');
      if (announcer) {
        announcer.textContent = message;
      }

      // Hide feedback after 3 seconds
      setTimeout(function() {
        feedback.style.opacity = '0';
      }, 3000);
    }
  };

  // =============================================================================
  // ACCESSIBILITY ENHANCEMENTS
  // =============================================================================

  var AccessibilityManager = {
    /**
     * PUBLIC_INTERFACE
     * Initializes accessibility features
     */
    init: function() {
      this.setupHighContrastMode();
      this.setupReducedMotion();
      this.setupKeyboardShortcuts();
      this.enhanceScreenReaderSupport();
    },

    /**
     * Sets up high contrast mode detection and handling
     */
    setupHighContrastMode: function() {
      if (window.matchMedia && window.matchMedia('(prefers-contrast: high)').matches) {
        document.documentElement.classList.add('high-contrast');
      }

      // Listen for changes
      if (window.matchMedia) {
        var mediaQuery = window.matchMedia('(prefers-contrast: high)');
        mediaQuery.addListener(function(e) {
          if (e.matches) {
            document.documentElement.classList.add('high-contrast');
          } else {
            document.documentElement.classList.remove('high-contrast');
          }
        });
      }
    },

    /**
     * Sets up reduced motion preferences
     */
    setupReducedMotion: function() {
      if (window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        document.documentElement.classList.add('reduced-motion');
      }
    },

    /**
     * Sets up additional keyboard shortcuts
     */
    setupKeyboardShortcuts: function() {
      document.addEventListener('keydown', function(e) {
        // Info shortcut (I key)
        if (e.key === 'i' || e.key === 'I') {
          e.preventDefault();
          AccessibilityManager.announcePageInfo();
        }

        // Help shortcut (H key)
        if (e.key === 'h' || e.key === 'H') {
          e.preventDefault();
          AccessibilityManager.announceHelp();
        }
      });
    },

    /**
     * Enhances screen reader support
     */
    enhanceScreenReaderSupport: function() {
      // Set up ARIA live regions
      var announcer = document.getElementById('announcements');
      if (!announcer) {
        announcer = document.createElement('div');
        announcer.id = 'announcements';
        announcer.setAttribute('aria-live', 'polite');
        announcer.setAttribute('aria-atomic', 'true');
        announcer.className = 'sr-only';
        document.body.appendChild(announcer);
      }

      // Announce page load
      setTimeout(function() {
        announcer.textContent = 'Página de información del programa cargada. Use las flechas para navegar y Enter para activar.';
      }, 1000);
    },

    /**
     * Announces current page information
     */
    announcePageInfo: function() {
      var title = document.querySelector('#program-title');
      var channel = document.querySelector('#layer-I4077-14475-456-11824-1-484');
      var time = document.querySelector('#layer-I4077-14476-456-15482-1-482');
      
      var info = 'Información del programa: ';
      if (title) info += title.textContent + ' en ';
      if (channel) info += channel.textContent + '. ';
      if (time) info += 'Hora actual: ' + time.textContent + '.';
      
      var announcer = document.getElementById('announcements');
      if (announcer) {
        announcer.textContent = info;
      }
    },

    /**
     * Announces help information
     */
    announceHelp: function() {
      var help = 'Ayuda de navegación: Use las flechas para navegar entre elementos. ' +
                 'Presione Enter o Espacio para activar. ' +
                 'Presione I para información de la página. ' +
                 'Presione Escape para regresar.';
      
      var announcer = document.getElementById('announcements');
      if (announcer) {
        announcer.textContent = help;
      }
    }
  };

  // =============================================================================
  // TV DISPLAY OPTIMIZATIONS
  // =============================================================================

  var TVOptimizations = {
    /**
     * PUBLIC_INTERFACE
     * Initializes TV-specific optimizations
     */
    init: function() {
      this.optimizeForTVDisplay();
      this.setupOverscanHandling();
      this.optimizeTextRendering();
    },

    /**
     * Applies TV-specific display optimizations
     */
    optimizeForTVDisplay: function() {
      // Ensure proper color gamut for TV displays
      document.documentElement.style.colorScheme = 'dark';
      
      // Optimize for TV viewing distance
      var style = document.createElement('style');
      style.textContent = `
        .tv-focused {
          outline: 4px solid #88c0d0 !important;
          outline-offset: 4px !important;
          border-radius: 12px !important;
          box-shadow: 0 0 20px rgba(136, 192, 208, 0.5) !important;
          transform: scale(1.05) !important;
          z-index: 100 !important;
          transition: all 0.2s ease !important;
        }
        
        .tv-activated {
          transform: scale(0.95) !important;
          transition: transform 0.1s ease !important;
        }
        
        @media (min-width: 1920px) {
          body { font-size: 110%; }
        }
        
        @media (min-width: 3840px) {
          body { font-size: 120%; }
        }
      `;
      document.head.appendChild(style);
    },

    /**
     * Handles TV overscan compensation
     */
    setupOverscanHandling: function() {
      var artboard = document.getElementById('artboard');
      if (artboard) {
        // Add safe area margins for overscan
        artboard.style.padding = '20px';
        artboard.style.boxSizing = 'border-box';
      }
    },

    /**
     * Optimizes text rendering for TV displays
     */
    optimizeTextRendering: function() {
      var style = document.createElement('style');
      style.textContent = `
        body {
          -webkit-font-smoothing: antialiased;
          -moz-osx-font-smoothing: grayscale;
          text-rendering: optimizeLegibility;
          font-variant-ligatures: none;
        }
        
        .typo-166, .typo-165, .typo-149 {
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.8);
        }
        
        .metadata-text, .hour-text {
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
        }
      `;
      document.head.appendChild(style);
    }
  };

  // =============================================================================
  // INITIALIZATION AND EVENT SETUP
  // =============================================================================

  /**
   * PUBLIC_INTERFACE
   * Main initialization function
   */
  function initializeApp() {
    // Core scaling functionality
    scaleArtboard();
    
    // Initialize all modules
    TVNavigation.init();
    ProgramActions.init();
    AccessibilityManager.init();
    TVOptimizations.init();
    
    console.log('Android TV app initialized successfully');
  }

  // Event listeners for different initialization scenarios
  window.addEventListener('resize', scaleArtboard);
  window.addEventListener('orientationchange', scaleArtboard);
  
  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeApp);
  } else {
    initializeApp();
  }

  // Expose public interfaces for debugging and external access
  window.__TVApp = {
    scaleArtboard: scaleArtboard,
    navigation: TVNavigation,
    actions: ProgramActions,
    accessibility: AccessibilityManager,
    optimizations: TVOptimizations,
    init: initializeApp
  };

})();
