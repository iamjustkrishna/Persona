# 🎨 Tools Section - Visual Preview

## Main Screen

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                                    ┃
┃  📱 Productivity Tools             ┃
┃  Smart tools to boost productivity ┃
┃                                    ┃
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃                                    ┃
┃  ╔═══════════╗  ╔═══════════╗     ┃
┃  ║    📥     ║  ║    🎤     ║     ┃
┃  ║           ║  ║           ║     ┃
┃  ║  Media    ║  ║  Voice    ║     ┃
┃  ║  Down     ║  ║  Memos    ║     ┃
┃  ║  loader   ║  ║           ║     ┃
┃  ║           ║  ║           ║     ┃
┃  ║ Download  ║  ║ Quick     ║     ┃
┃  ║ videos &  ║  ║ voice     ║     ┃
┃  ║ audio...  ║  ║ notes...  ║     ┃
┃  ╚═══════════╝  ╚═══════════╝     ┃
┃                                    ┃
┃  ╔═══════════╗  ╔═══════════╗     ┃
┃  ║ ✅  [Soon]║  ║ 🏆  [Soon]║     ┃
┃  ║  (gray)   ║  ║  (gray)   ║     ┃
┃  ║  Habit    ║  ║  Goal     ║     ┃
┃  ║  Tracker  ║  ║  Setter   ║     ┃
┃  ║           ║  ║           ║     ┃
┃  ║ Build &   ║  ║ Set and   ║     ┃
┃  ║ track...  ║  ║ achieve.. ║     ┃
┃  ╚═══════════╝  ╚═══════════╝     ┃
┃                                    ┃
┃  ╔═══════════╗  ╔═══════════╗     ┃
┃  ║ 📝  [Soon]║  ║ 🔗  [Soon]║     ┃
┃  ║  (gray)   ║  ║  (gray)   ║     ┃
┃  ║  Quick    ║  ║  Link     ║     ┃
┃  ║  Notes    ║  ║  Saver    ║     ┃
┃  ║           ║  ║           ║     ┃
┃  ║ Jot down  ║  ║ Save      ║     ┃
┃  ║ ideas...  ║  ║ import... ║     ┃
┃  ╚═══════════╝  ╚═══════════╝     ┃
┃                                    ┃
┃  ╔═══════════╗  ╔═══════════╗     ┃
┃  ║ 🔢  [Soon]║  ║ 📷  [Soon]║     ┃
┃  ║  (gray)   ║  ║  (gray)   ║     ┃
┃  ║  Calc     ║  ║  QR       ║     ┃
┃  ║  ulator   ║  ║  Scanner  ║     ┃
┃  ║           ║  ║           ║     ┃
┃  ║ Quick     ║  ║ Scan QR   ║     ┃
┃  ║ calcs...  ║  ║ codes...  ║     ┃
┃  ╚═══════════╝  ╚═══════════╝     ┃
┃                                    ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

---

## Interaction States

### 1. Tapping Active Tool (Media Downloader)

**Before Tap:**
```
┌─────────────────┐
│   📥           │  ← Solid color
│                │
│ Media          │
│ Downloader     │
│                │
│ Download       │
│ videos & audio │
└─────────────────┘
```

**After Tap:**
```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ ← Media Downloader           ┃ ← Top bar with back
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃                              ┃
┃  [Enter Video URL________]  ┃
┃                              ┃
┃  ○ Video  ○ Audio           ┃
┃                              ┃
┃  [Get video] button          ┃
┃                              ┃
┃  (Preview area)              ┃
┃                              ┃
┃  [Download video] button     ┃
┃                              ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

### 2. Tapping Coming Soon Tool

**Before Tap:**
```
┌─────────────────┐
│ ✅    [Soon]   │  ← Grayed out
│  (muted)       │
│ Habit          │
│ Tracker        │
│                │
│ Build & track  │
│ daily habits   │
└─────────────────┘
```

**After Tap:**
```
       ┌─────────────────┐
       │ Coming Soon! 🚀 │ ← Toast message
       └─────────────────┘
(Card stays on screen, no navigation)
```

---

## Card Anatomy

### Active Tool Card
```
┌────────────────────────────────────┐
│ ┌──────┐                           │ ← Top Row
│ │ 📥   │                           │   - Icon container (56dp)
│ │      │                           │   - Colored background
│ └──────┘                           │   - Large icon (28dp)
│                                    │
│ ─────────────────────────── (space)│
│                                    │
│ Media Downloader            Bold   │ ← Title
│ Download videos & audio     Light  │ ← Description
│ from social media           (2 ln) │   (max 2 lines)
│                                    │
└────────────────────────────────────┘
 ↑ Shadow: 4dp elevation
```

### Coming Soon Card
```
┌────────────────────────────────────┐
│ ┌──────┐              ┌────────┐  │ ← Top Row
│ │ ✅   │  50% opacity │ Soon   │  │   - Icon + Badge
│ │      │              └────────┘  │
│ └──────┘                           │
│                                    │
│ ─────────────────────────── (space)│
│                                    │
│ Habit Tracker           60% opac.  │ ← Muted text
│ Build & track daily     50% opac.  │
│ habits                             │
│                                    │
└────────────────────────────────────┘
 ↑ Shadow: 0dp (flat)
```

---

## Color Palette

### Light Mode
```
Active Cards:
  Background: #FFFFFF
  Icon BG:    #E3F2FD (primary container)
  Icon:       #1976D2 (primary)
  Text:       #000000
  Shadow:     rgba(0,0,0,0.1)

Coming Soon:
  Background: #F5F5F5 (muted)
  Icon BG:    #E0E0E0
  Icon:       #9E9E9E (50% opacity)
  Text:       #616161 (60% opacity)
  Badge BG:   #FFE0B2
  Badge Text: #E65100
```

### Dark Mode
```
Active Cards:
  Background: #1E1E1E
  Icon BG:    #1A237E (primary container)
  Icon:       #64B5F6 (primary)
  Text:       #FFFFFF
  Shadow:     rgba(255,255,255,0.1)

Coming Soon:
  Background: #2C2C2C (muted)
  Icon BG:    #424242
  Icon:       #757575 (50% opacity)
  Text:       #9E9E9E (60% opacity)
  Badge BG:   #5D4037
  Badge Text: #FFAB91
```

---

## Responsive Behavior

### Portrait (Phone)
```
Grid: 2 columns
Card size: ~170dp x 180dp
Spacing: 12dp
Padding: 16dp

┌─────┬─────┐
│  1  │  2  │
├─────┼─────┤
│  3  │  4  │
├─────┼─────┤
│  5  │  6  │
├─────┼─────┤
│  7  │  8  │
└─────┴─────┘
```

### Landscape (Tablet)
```
Grid: 4 columns
Card size: Auto x 180dp
Spacing: 12dp

┌───┬───┬───┬───┐
│ 1 │ 2 │ 3 │ 4 │
├───┼───┼───┼───┤
│ 5 │ 6 │ 7 │ 8 │
└───┴───┴───┴───┘
```

---

## Animation Timings

### Card Press
```
Scale: 1.0 → 0.95 (100ms)
Release: 0.95 → 1.0 (150ms)
```

### Screen Transition
```
Slide in: 300ms ease-out
Slide out: 250ms ease-in
```

### Coming Soon Toast
```
Fade in: 200ms
Display: 2000ms
Fade out: 300ms
```

### Card Load
```
Cards: Stagger 50ms each
Total: ~400ms for all 8
```

---

## Icon Reference

### Current Icons
```
📥 Media Downloader   → Icons.Rounded.Download
🎤 Voice Memos        → Icons.Rounded.Mic
✅ Habit Tracker      → Icons.Rounded.TaskAlt
🏆 Goal Setter        → Icons.Rounded.EmojiEvents
📝 Quick Notes        → Icons.Rounded.Note
🔗 Link Saver         → Icons.Rounded.Link
🔢 Calculator         → Icons.Rounded.Calculate
📷 QR Scanner         → Icons.Rounded.QrCodeScanner
```

### Alternative Icons (if needed)
```
📊 Analytics          → Icons.Rounded.Analytics
⏰ Timer              → Icons.Rounded.Timer
🌍 World Clock        → Icons.Rounded.Public
💱 Currency           → Icons.Rounded.CurrencyExchange
📐 Unit Converter     → Icons.Rounded.SwapHoriz
🎨 Color Picker       → Icons.Rounded.Palette
📸 Image Notes        → Icons.Rounded.Camera
📋 Checklist          → Icons.Rounded.Checklist
```

---

## Accessibility

### Screen Reader
```
Active Card:
  "Media Downloader. Download videos and audio from social media. Double tap to open."

Coming Soon Card:
  "Habit Tracker. Coming Soon. Build and track daily habits."
```

### Touch Targets
```
Minimum: 48dp x 48dp
Cards: 170dp x 180dp (plenty!)
Tap area: Entire card
```

### Color Contrast
```
Active cards: AAA compliant
Coming Soon: AA compliant (intentionally muted)
Text: Meets WCAG 2.1 standards
```

---

## User Feedback

### Tap Active Card
```
1. Visual: Card scales down slightly
2. Haptic: Light impact (if available)
3. Action: Navigate to tool screen
4. Duration: < 300ms
```

### Tap Coming Soon
```
1. Visual: Card scales down slightly
2. Haptic: Warning vibration
3. Action: Show toast
4. Duration: Instant
```

### Long Press (Future)
```
Potential feature:
  Long press → Show tool details
  Without navigating
```

---

## Edge Cases

### Empty State (No Tools)
```
Not applicable - tools are hardcoded
Always shows 8 tools
```

### Network Required (Media Downloader)
```
No internet → Show error in detail screen
"No internet connection. Please check your network."
```

### Permission Required (Voice Memos)
```
No mic permission → Prompt user
"Microphone access needed to record memos"
[Grant Permission] button
```

### Storage Full
```
Can't save → Show error
"Storage full. Free up space and try again."
```

---

## Performance

### Load Time
```
Tools screen: < 100ms (instant)
Card animations: 50ms stagger
Total perceived load: < 500ms
```

### Memory Usage
```
Static tool list: Minimal
No images to load: Fast
Icons: Vector (small)
```

### Scroll Performance
```
Grid: LazyVerticalGrid
Only renders visible cards
Smooth 60fps scrolling
```

---

## Testing Checklist

### Visual
- [ ] Cards render correctly
- [ ] Icons load properly
- [ ] "Soon" badges show
- [ ] Colors match design
- [ ] Spacing consistent

### Interaction
- [ ] Active cards navigate
- [ ] Coming Soon shows toast
- [ ] Back button works
- [ ] Animations smooth
- [ ] No lag or jank

### Responsive
- [ ] Portrait: 2 columns
- [ ] Landscape: Adjusts
- [ ] Tablet: Larger grid
- [ ] No overflow issues

### Accessibility
- [ ] Screen reader works
- [ ] Touch targets adequate
- [ ] Color contrast good
- [ ] Navigable by keyboard (web)

---

## Quick Reference

### Key Measurements
```
Card height: 180dp
Card spacing: 12dp
Screen padding: 16dp
Icon container: 56dp
Icon size: 28dp
Corner radius: 20dp
Badge radius: 8dp
```

### Key Colors
```
Primary: #1976D2 (blue)
Error: #D32F2F (red)
Surface: #FFFFFF (white)
On Surface: #000000 (black)
```

### Key Durations
```
Card press: 100ms
Card release: 150ms
Navigation: 300ms
Toast: 2000ms
```

---

**This is the new Tools section!** 🔥

Modern, intuitive, and ready to scale. Users will love the organization and clear communication about what's available now vs. coming soon.

**Build → Test → Ship!** 🚀
