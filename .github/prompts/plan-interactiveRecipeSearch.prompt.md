# Plan: Add Interactive Recipe Search to Homepage

**TL;DR:** Add a search box to index.html with real-time recipe search (debounced, minimum 2 chars). Fetch from the existing `/api/recipes/search?query={term}` endpoint and display results as expandable cards below the search box. Include autocomplete suggestions (max 10), smooth fade-in animations, mobile-responsive design, and "No recipes found" fallback. JavaScript handles debouncing and DOM updates; CSS uses the established purple gradient theme with elevation hover effects.

## Steps

### 1. Modify index.html - Add search section after the welcome div

- Insert search container with input field and results placeholder
- Add CSS for search box styling (gradient border, focus states), results grid, "no results" message, and fade-in animations
- Ensure mobile-first responsive design with `grid-template-columns: repeat(auto-fit, minmax(250px, 1fr))`

### 2. Add JavaScript to index.html - Implement search functionality

- Create debounce utility function (500ms delay)
- Listen to input events with minimum 2 character check
- Fetch from `/api/recipes/search?query=${term}` endpoint
- Handle API success (render up to 10 results) and errors (show error message)
- Clear results when input is empty
- Render recipe cards with name, cuisine, and difficulty
- When recipe card is clicked, expand to show full details inline (append description, prep/cook time, servings)

### 3. Style CSS additions - Match existing purple gradient theme

- Search box: semi-transparent background with purple gradient border focus state
- Results container: fade-in animation (0.3-0.5s)
- Recipe cards: reuse existing `.feature-card` styles or extend them for results
- Difficulty badges: color-coded (Easy=green, Medium=yellow, Hard=red)
- Hover effects: elevation (`translateY(-5px)`) matching existing card behavior
- Empty state: center "No recipes found" message with icon
- Mobile: stack results vertically, reduce padding on mobile viewports

### 4. Verify mobile responsiveness

- Test on mobile viewport (375px width)
- Ensure search box and results are readable
- Check that animations don't cause jank on lower-end devices
- Ensure cards stack properly when width is constrained

## Verification

- **Manual testing**: Open homepage, type in search box (test with <2 chars for no results, ≥2 chars to trigger API calls)
- **API testing**: Verify `/api/recipes/search?query=pasta` returns expected results
- **Animation testing**: Confirm fade-in effect when results appear, elevation on hover
- **Mobile testing**: Test on mobile browser or DevTools mobile emulation
- **Edge cases**: Empty results, network errors, minimum character threshold, rapid typing (debounce)

## Decisions

- Chose 500ms debounce to balance responsiveness vs API load (standard UX pattern)
- Set minimum 2 characters to reduce unnecessary API calls while maintaining usability
- Capped results at 10 suggestions to keep UI clean (autocomplete best practice)
- Reuse existing feature card styling for consistency with purple-gradient theme
- Inline card expansion on click vs navigation to keep users on homepage for quick search-and-explore flow
- Used vanilla JavaScript (no dependencies) matching existing project patterns

## Technical Details

**Selected Options:**

- Autocomplete: Live search on every keystroke with 500ms debounce, minimum 2 characters
- Click action: Expand card details on homepage
- Search scope: Use existing name-based search API

**Search Endpoint:** GET `/api/recipes/search?query={term}` (already implemented in RecipeController)

**Recipe Model Fields Used:**

- `name` - Displayed in card header
- `cuisineType` - Displayed as badge
- `difficultyLevel` - Color-coded badge (Easy/Medium/Hard)
- `description` - Shown on expansion
- `prepTime` - Shown on expansion
- `cookTime` - Shown on expansion
- `servings` - Shown on expansion
