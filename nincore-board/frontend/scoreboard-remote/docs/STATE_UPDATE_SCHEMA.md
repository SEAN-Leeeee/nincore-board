# STATE_UPDATE Payload Schema

`STATE_UPDATE` payload uses a fixed top-level key set.

## Keys
- `quarter`, `gameTime`, `shotClock`
- `isGameRunning`, `isShotRunning`
- `homeScore`, `homeFoul`, `homeAssists`, `homeRebounds`, `homeSteals`
- `awayScore`, `awayFoul`, `awayAssists`, `awayRebounds`, `awaySteals`
- `players`, `rosterPlayers`
- `homeName`, `awayName`
- `gameLog`
- `everActivePlayerIds`

## Rules
- Sender should publish the full payload shape for state snapshots.
- Receiver must ignore unknown keys.
- Missing keys in partial updates must not overwrite existing local state.
- `players`/`rosterPlayers` are team-keyed objects (`Home`, `Away`).
- `everActivePlayerIds` is serialized as arrays (`Home: string[]`, `Away: string[]`).

## Implementation
- Schema source: `src/shared/stateUpdateSchema.js`
- Snapshot builder: `createStateUpdatePayloadFromLocal(...)`
- Incoming payload filter: `normalizeStateUpdatePayload(...)`
