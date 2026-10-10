---
name: MoneyFlow Clay & Light
colors:
  surface: '#fcf9f4'
  surface-dim: '#dcdad5'
  surface-bright: '#fcf9f4'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3ee'
  surface-container: '#f0ede9'
  surface-container-high: '#ebe8e3'
  surface-container-highest: '#e5e2dd'
  on-surface: '#1c1c19'
  on-surface-variant: '#424843'
  inverse-surface: '#31302d'
  inverse-on-surface: '#f3f0eb'
  outline: '#727972'
  outline-variant: '#c2c8c1'
  surface-tint: '#446650'
  primary: '#42634d'
  on-primary: '#ffffff'
  primary-container: '#5a7c65'
  on-primary-container: '#f5fff4'
  inverse-primary: '#abcfb5'
  secondary: '#8d4e2f'
  on-secondary: '#ffffff'
  secondary-container: '#fdab85'
  on-secondary-container: '#783d1f'
  tertiary: '#325f86'
  on-tertiary: '#ffffff'
  tertiary-container: '#4d78a0'
  on-tertiary-container: '#fcfcff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c6ecd0'
  primary-fixed-dim: '#abcfb5'
  on-primary-fixed: '#002110'
  on-primary-fixed-variant: '#2d4e39'
  secondary-fixed: '#ffdbcc'
  secondary-fixed-dim: '#ffb695'
  on-secondary-fixed: '#351000'
  on-secondary-fixed-variant: '#70371a'
  tertiary-fixed: '#cee5ff'
  tertiary-fixed-dim: '#9fcbf6'
  on-tertiary-fixed: '#001d33'
  on-tertiary-fixed-variant: '#194a6f'
  background: '#fcf9f4'
  on-background: '#1c1c19'
  surface-variant: '#e5e2dd'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 44px
    fontWeight: '700'
    lineHeight: 52px
    letterSpacing: -0.03em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 42px
    letterSpacing: -0.025em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
  numeric-hero:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.03em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system expresses financial wellness through a tactile, organic, and ultra-soothing lens. It rejects the cold, sterile, and anxiety-inducing look of traditional banking interfaces in favor of soft claymorphism and refined neumorphism. Surfaces mimic sculpted porcelain, matte clay, and satin paper.

The aesthetic evokes calm confidence, approachability, and mindful wealth management. The target audience comprises modern digital natives, international freelancers, and thoughtful investors who seek order, beauty, and emotional peace in their daily financial rituals.

Key visual pillars:
- **Tactile Clay Surfaces:** Rounded volumes that appear gently extruded from the warm background canvas.
- **Micro-Specular Rim Highlights:** Subtle, directional top-edge inner highlights paired with ambient drop shadows to produce an inviting physical feel without skeuomorphic clutter.
- **Harmonious Pastel Accents:** Functional pastel tones (soft peach, powder blue, fresh mint, vibrant coral) providing distinct category tags and metric cues against quiet ivory layers.
- **Balanced Warmth:** High-contrast warm charcoal text replaces harsh absolute blacks, preserving effortless legibility while maintaining the overall gentle demeanor.

## Colors

The palette is anchored by warm mineral tones and soft culinary ceramics.

- **Primary (`#5A7C65`):** A muted eucalyptus sage green representing steady growth, positive balances, safe investments, and primary interactions.
- **Secondary (`#F3A27D`):** A soft warm peach for lifestyle expenses, notifications, and playful secondary highlights.
- **Tertiary (`#7CA7D1`):** A breathable powder blue used for recurring subscriptions, utilities, savings targets, and secondary analytics.
- **Semantic Coral (`#E76F51`):** A softened terracotta coral for budget caps exceeded, alert warnings, and debt alerts.
- **Neutral Canvas (`#FAF7F2`):** A rich, warm ivory background establishing the foundational light plane.
- **Neutral Card Surface (`#FFFFFF`):** Pure matte porcelain white, gently floating above the ivory canvas.
- **Neutral Sunk Surface (`#F0ECE4`):** Recessed wells, progress troughs, and depressed button states.
- **Text & Content Primaries:** `#2D312E` (Deep warm charcoal) for headline anchors and primary figures; `#5C615D` (Muted slate stone) for supporting metadata and secondary labels.

## Typography

The design system uses **Plus Jakarta Sans** consistently across all roles. Its geometric underpinnings softened by friendly, open apertures complement the plump curves of claymorphic geometry while maintaining crisp horizontal rhythm for dense financial data tables and account balances.

Formatting rules:
- **Financial Balance Numerals:** Always set with tabular figures (`font-variant-numeric: tabular-nums`) to prevent shifting layouts across live ticker movements and fluctuating totals.
- **Hierarchy Stacking:** Pair heavy display weights (`700`) on monetary integers with lighter supporting weights (`500` or `400`) on fractional decimals and currency symbols.
- **All-Caps Restraint:** Restrict uppercase usage strictly to `label-sm` for category flags and ledger badges, accompanied by generous letter-spacing to sustain readability.

## Layout & Spacing

The layout is built around a mobile-first, card-centric fluid column model. Content runs edge-to-edge inside safe-area boundaries, framed by comfortable canvas margins that let clay components breath without cluttering compact smartphone screens.

- **Mobile Viewports (<600px):** Single-column layout with 4-column subgrids for quick action tiles and analytics cards. Fixed outer margins of `1.25rem` (`20px`).
- **Tablet/Desktop Adaptive Frames (>=600px):** Centered application frame maxing out at `480px` on tablets to preserve intimate handheld ergonomics, expanding to an aligned 2-column dashboard layout (8-column fluid grid, `1.5rem` gutters) on expanded workspace views.
- **Vertical Rhythm:** Strict adherence to an 8pt layout module (`0.5rem`, `1rem`, `1.5rem`, `2rem`). Card padding remains spacious (`1.25rem` to `1.5rem`) to accommodate the internal perimeter light highlights and soft outer drop shadows without overlapping sibling elements.

## Elevation & Depth

Depth in this system is tactile, pneumatic, and physical rather than optical or paper-thin. Elevation is generated through dual-opposing light sources: a dominant light source from the top-left (315 degrees) casting diffused warm ambient shadows downward, accompanied by an upward reflection from the ivory ground.

- **Level 0 (Recessed / Inset Wells):** Used for input fields, segmented control backplates, and empty progress tracks.
  - Box Shadow: `inset 2px 3px 6px rgba(184, 175, 161, 0.45), inset -2px -2px 5px rgba(255, 255, 255, 0.8)`
- **Level 1 (Extruded Clay Cards & Tiles):** Resting state for primary transaction cards and balance containers.
  - Box Shadow: `6px 8px 18px rgba(199, 190, 175, 0.35), -4px -4px 14px rgba(255, 255, 255, 0.95), inset 1px 1px 2px rgba(255, 255, 255, 0.8)`
- **Level 2 (Elevated Clay Buttons & Floating Trays):** Active pills, primary CTA buttons, and floating bottom navigation bars.
  - Box Shadow: `8px 12px 24px rgba(184, 175, 161, 0.4), -6px -6px 16px rgba(255, 255, 255, 1.0), inset 1.5px 1.5px 2px rgba(255, 255, 255, 0.9)`
- **Level 3 (Modals & Bottom Drawers):** High-priority contextual overlays.
  - Box Shadow: `0 20px 40px rgba(148, 140, 128, 0.3), 0 -2px 10px rgba(255, 255, 255, 0.8)`

## Shapes

The shape system adopts smooth, bulbous, and ergonomic pill geometries (`roundedness: 3`). Sharp angles are strictly forbidden to preserve the soft, hand-molded clay metaphor.

- **Micro Shapes (Badges, Chips, Quick Toggles):** Fully rounded pill geometry (`9999px` border radius).
- **Interactive Buttons & Form Controls:** Smooth pill profile (`9999px` border radius) or heavy rounded corners (`20px`).
- **Standard Cards & Module Enclosures:** Continuous squircle-like corners set to `24px` on small cards and `28px` to `32px` on large metric modules.
- **Overlays & Bottom Sheets:** Sweeping `36px` radius applied to top-left and top-right apex corners.

## Components

### Buttons
- **Primary Action (Clay Pill):** Extruded primary sage green (`#5A7C65`) or deep warm charcoal (`#2D312E`) background with pure white text. High tactile elevation (Level 2). Pressed state collapses the outer shadow and applies an inner shade to feel physically compressed.
- **Secondary Action (Soft Clay Pill):** Matte white surface, Level 1 elevation, `#2D312E` label.
- **Ghost/Tertiary:** No background; displays an active pastel icon container and warm slate label.

### Cards
- **Account & Cash Flow Cards:** White ceramic finish (`#FFFFFF`) with a `24px` to `32px` border radius, resting at Level 1 elevation. Includes a top-edge inner specular highlight (`inset 1px 1.5px 0px rgba(255, 255, 255, 1.0)`).
- **Highlight Metric Tiles:** Soft pastel-tinted clay surfaces (e.g., `#FAF0EB` for peach insights, `#EDF4FA` for blue balances) paired with tonal accent glyphs.

### Chips & Filter Pills
- Level 0 (recessed) in unselected states with `#5C615D` text.
- Elevates to Level 1 clay extrusion with crisp white background and primary text when active.

### Input Fields
- Sunken Level 0 inset wells with `#F0ECE4` background, `20px` corner radius, and subtle top-left inner shadow.
- On focus: Transition to a crisp white fill with an ambient pastel green glow (`0 0 0 3px rgba(90, 124, 101, 0.2)`).

### Checkboxes & Segmented Controls
- **Segmented Control Bar:** Inset recessed track (`#ECE7DE`) housing a floating white pill slider that glides between options using smooth spring physics.
- **Radio & Switches:** Soft toggle switches styled as a circular extruded clay marble sliding inside a recessed track.

### Lists & Activity Feeds
- Avoid hairline dividers. Items are visually grouped as individual pill-cards or separated by generous whitespace (`12px` gaps) over the ivory background.
- Left-aligned circular category badges (`48px` diameter) with custom pastel icon backdrops.

### Specialized Financial Components
- **Pneumatic Gauge & Progress Bars:** Inset recessed groove track with a plump, rounded colored clay pill bar advancing along it.
- **Tactile Dial Pad:** Individual circular Level 1 clay buttons providing haptic and visual compression feedback upon touch.